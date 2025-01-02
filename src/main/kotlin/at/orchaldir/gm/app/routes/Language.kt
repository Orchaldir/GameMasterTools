package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LANGUAGES
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.field
import at.orchaldir.gm.app.html.model.fieldCreator
import at.orchaldir.gm.app.html.model.selectCreator
import at.orchaldir.gm.app.html.model.selectDate
import at.orchaldir.gm.app.parse.parseLanguage
import at.orchaldir.gm.core.action.CreateLanguage
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.*
import at.orchaldir.gm.core.selector.*
import at.orchaldir.gm.core.selector.item.countBooks
import at.orchaldir.gm.core.selector.item.getBooks
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
class LanguageRoutes {
    @Resource("details")
    class Details(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    @Resource("new")
    class New(val parent: LanguageRoutes = LanguageRoutes())

    @Resource("delete")
    class Delete(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    @Resource("edit")
    class Edit(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    @Resource("preview")
    class Preview(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())

    @Resource("update")
    class Update(val id: LanguageId, val parent: LanguageRoutes = LanguageRoutes())
}

fun Application.configureLanguageRouting() {
    routing {
        get<LanguageRoutes> {
            logger.info { "Get all languages" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllLanguages(call, STORE.getState())
            }
        }
        get<LanguageRoutes.Details> { details ->
            logger.info { "Get details of language ${details.id.value}" }

            val state = STORE.getState()
            val language = state.getLanguageStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLanguageDetails(call, state, language)
            }
        }
        get<LanguageRoutes.New> {
            logger.info { "Add new language" }

            STORE.dispatch(CreateLanguage)

            call.respondRedirect(
                call.application.href(
                    LanguageRoutes.Edit(
                        STORE.getState().getLanguageStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<LanguageRoutes.Delete> { delete ->
            logger.info { "Delete language ${delete.id.value}" }

            STORE.dispatch(DeleteLanguage(delete.id))

            call.respondRedirect(call.application.href(LanguageRoutes()))

            STORE.getState().save()
        }
        get<LanguageRoutes.Edit> { edit ->
            logger.info { "Get editor for language ${edit.id.value}" }

            val state = STORE.getState()
            val language = state.getLanguageStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLanguageEditor(call, state, language)
            }
        }
        post<LanguageRoutes.Preview> { preview ->
            logger.info { "Preview changes to language ${preview.id.value}" }

            val state = STORE.getState()
            val language = parseLanguage(call.receiveParameters(), state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {

                showLanguageEditor(call, state, language)
            }
        }
        post<LanguageRoutes.Update> { update ->
            logger.info { "Update language ${update.id.value}" }

            val language = parseLanguage(call.receiveParameters(), STORE.getState(), update.id)

            STORE.dispatch(UpdateLanguage(language))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllLanguages(
    call: ApplicationCall,
    state: State,
) {
    val languages = STORE.getState().getLanguageStorage().getAll().sortedBy { it.name }
    val count = languages.size
    val createLink = call.application.href(LanguageRoutes.New())

    simpleHtml("Languages") {
        field("Count", count.toString())

        table {
            tr {
                th { +"Name" }
                th { +"Books" }
                th { +"Characters" }
                th { +"Cultures" }
            }
            languages.forEach { language ->
                tr {
                    td { link(call, state, language) }
                    tdSkipZero(state.countBooks(language.id))
                    tdSkipZero(state.countCharacters(language.id))
                    tdSkipZero(state.countCultures(language.id))
                }
            }
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
    val backLink = call.application.href(LanguageRoutes())
    val deleteLink = call.application.href(LanguageRoutes.Delete(language.id))
    val editLink = call.application.href(LanguageRoutes.Edit(language.id))
    val children = state.getChildren(language.id)
    val books = state.getBooks(language.id)
    val characters = state.getCharacters(language.id)
    val cultures = state.getCultures(language.id)

    simpleHtml("Language: ${language.name}") {
        field("Name", language.name)
        when (val origin = language.origin) {
            is CombinedLanguage -> {
                field("Origin", "Combined")
                showList("Parent Languages", origin.parents) { id ->
                    link(call, state, id)
                }
            }

            is EvolvedLanguage -> {
                field("Origin", "Evolved")
                fieldLink("Parent Language", call, state, origin.parent)
            }

            is InventedLanguage -> {
                field("Origin", "Invented")
                fieldCreator(call, state, origin.inventor, "Inventor")
                field(call, state, "Date", origin.date)
            }

            OriginalLanguage -> {
                field("Origin", "Original")
            }
        }
        showList("Child Languages", children) { language ->
            link(call, language)
        }
        h2 { +"Usage" }
        showList("Books", books) { book ->
            link(call, state, book)
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
    val previewLink = call.application.href(LanguageRoutes.Preview(language.id))
    val updateLink = call.application.href(LanguageRoutes.Update(language.id))

    simpleHtml("Edit Language: ${language.name}") {
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
            when (val origin = language.origin) {
                is CombinedLanguage -> {
                    possibleParents.sortedBy { it.name }.forEach { l ->
                        p {
                            checkBoxInput {
                                name = LANGUAGES
                                value = l.id.value.toString()
                                checked = origin.parents.contains(l.id)
                                +l.name
                            }
                        }
                    }
                }

                is EvolvedLanguage ->
                    selectValue("Parent", LANGUAGES, possibleParents) { l ->
                        label = l.name
                        value = l.id.value.toString()
                        selected = origin.parent == l.id
                    }

                is InventedLanguage -> {
                    selectCreator(state, origin.inventor, language.id, origin.date, "Inventor")
                    selectDate(state, "Date", origin.date, DATE)
                }

                else -> doNothing()
            }
            button("Update", updateLink)
        }
        back(backLink)
    }
}