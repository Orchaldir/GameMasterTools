package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.INVENTOR
import at.orchaldir.gm.app.LANGUAGES
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseLanguage
import at.orchaldir.gm.core.action.CreateLanguage
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.*
import at.orchaldir.gm.core.selector.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/languages")
class Languages {
    @Resource("details")
    class Details(val id: LanguageId, val parent: Languages = Languages())

    @Resource("new")
    class New(val parent: Languages = Languages())

    @Resource("delete")
    class Delete(val id: LanguageId, val parent: Languages = Languages())

    @Resource("edit")
    class Edit(val id: LanguageId, val parent: Languages = Languages())

    @Resource("preview")
    class Preview(val id: LanguageId, val parent: Languages = Languages())

    @Resource("update")
    class Update(val id: LanguageId, val parent: Languages = Languages())
}

fun Application.configureLanguageRouting() {
    routing {
        get<Languages> {
            logger.info { "Get all languages" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllLanguages(call)
            }
        }
        get<Languages.Details> { details ->
            logger.info { "Get details of language ${details.id.value}" }

            val state = STORE.getState()
            val language = state.getLanguageStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLanguageDetails(call, state, language)
            }
        }
        get<Languages.New> {
            logger.info { "Add new language" }

            STORE.dispatch(CreateLanguage)

            call.respondRedirect(call.application.href(Languages.Edit(STORE.getState().getLanguageStorage().lastId)))

            STORE.getState().save()
        }
        get<Languages.Delete> { delete ->
            logger.info { "Delete language ${delete.id.value}" }

            STORE.dispatch(DeleteLanguage(delete.id))

            call.respondRedirect(call.application.href(Languages()))

            STORE.getState().save()
        }
        get<Languages.Edit> { edit ->
            logger.info { "Get editor for language ${edit.id.value}" }

            val state = STORE.getState()
            val language = state.getLanguageStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLanguageEditor(call, state, language)
            }
        }
        post<Languages.Preview> { preview ->
            logger.info { "Preview changes to language ${preview.id.value}" }

            val language = parseLanguage(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()

                showLanguageEditor(call, state, language)
            }
        }
        post<Languages.Update> { update ->
            logger.info { "Update language ${update.id.value}" }

            val language = parseLanguage(update.id, call.receiveParameters())

            STORE.dispatch(UpdateLanguage(language))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllLanguages(call: ApplicationCall) {
    val languages = STORE.getState().getLanguageStorage().getAll().sortedBy { it.name }
    val count = languages.size
    val createLink = call.application.href(Languages.New())

    simpleHtml("Languages") {
        field("Count", count.toString())
        showList(languages) { language ->
            link(call, language)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showLanguageDetails(
    call: ApplicationCall,
    state: State,
    language: Language,
) {
    val backLink = call.application.href(Languages())
    val deleteLink = call.application.href(Languages.Delete(language.id))
    val editLink = call.application.href(Languages.Edit(language.id))
    val children = state.getChildren(language.id)
    val characters = state.getCharacters(language.id)
    val cultures = state.getCultures(language.id)

    simpleHtml("Language: ${language.name}") {
        field("Id", language.id.value.toString())
        field("Name", language.name)
        when (language.origin) {
            is CombinedLanguage -> {
                field("Origin", "Combined")
                showList("Parent Languages", language.origin.parents) { id ->
                    link(call, state, id)
                }
            }

            is EvolvedLanguage -> {
                field("Origin", "Evolved")
                field("Parent Language") {
                    link(call, state, language.origin.parent)
                }
            }

            is InventedLanguage -> {
                field("Origin", "Invented")
                field("Inventor") {
                    link(call, state, language.origin.inventor)
                }
            }

            OriginalLanguage -> {
                field("Origin", "Original")
            }
        }
        showList("Child Languages", children) { language ->
            link(call, language)
        }
        showList("Characters", characters) { character ->
            link(call, state, character)
        }
        showList("Cultures", cultures) { culture ->
            link(call, culture)
        }
        action(editLink, "Edit")
        if (state.canDelete(language.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showLanguageEditor(
    call: ApplicationCall,
    state: State,
    language: Language,
) {
    val possibleInventors = state.getCharacterStorage().getAll()
    val possibleParents = state.getPossibleParents(language.id)
    val backLink = href(call, language.id)
    val previewLink = call.application.href(Languages.Preview(language.id))
    val updateLink = call.application.href(Languages.Update(language.id))

    simpleHtml("Edit Language: ${language.name}") {
        field("Id", language.id.value.toString())
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            selectName(language.name)
            field("Origin") {
                select {
                    id = ORIGIN
                    name = ORIGIN
                    onChange = ON_CHANGE_SCRIPT
                    LanguageOriginType.entries.forEach { origin ->
                        option {
                            label = origin.name
                            value = origin.name
                            disabled = when (origin) {
                                LanguageOriginType.Combined -> possibleParents.size < 2
                                LanguageOriginType.Evolved -> possibleParents.isEmpty()
                                LanguageOriginType.Invented -> possibleInventors.isEmpty()
                                LanguageOriginType.Original -> false
                            }
                            selected = when (origin) {
                                LanguageOriginType.Combined -> language.origin is OriginalLanguage
                                LanguageOriginType.Evolved -> language.origin is EvolvedLanguage
                                LanguageOriginType.Invented -> language.origin is InventedLanguage
                                LanguageOriginType.Original -> language.origin is CombinedLanguage
                            }
                        }
                    }
                }
            }
            when (language.origin) {
                is CombinedLanguage -> {
                    possibleParents.sortedBy { it.name }.forEach { l ->
                        p {
                            checkBoxInput {
                                name = LANGUAGES
                                value = l.id.value.toString()
                                checked = language.origin.parents.contains(l.id)
                                +l.name
                            }
                        }
                    }
                }

                is EvolvedLanguage ->
                    selectValue("Parent", LANGUAGES, possibleParents) { l ->
                        label = l.name
                        value = l.id.value.toString()
                        selected = language.origin.parent == l.id
                    }

                is InventedLanguage -> {
                    selectValue(
                        "Inventor",
                        INVENTOR,
                        possibleInventors
                    ) { c ->
                        label = state.getName(c)
                        value = c.id.value.toString()
                        selected = language.origin.inventor == c.id
                    }
                }

                else -> doNothing()
            }
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        back(backLink)
    }
}