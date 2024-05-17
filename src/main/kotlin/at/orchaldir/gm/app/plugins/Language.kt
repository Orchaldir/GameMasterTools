package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.core.action.CreateLanguage
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.language.*
import at.orchaldir.gm.core.selector.getChildren
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/languages")
class Languages {
    @Resource("details")
    class Details(val parent: Languages = Languages(), val id: LanguageId)

    @Resource("new")
    class New(val parent: Languages = Languages())

    @Resource("delete")
    class Delete(val parent: Languages = Languages(), val id: LanguageId)

    @Resource("edit")
    class Edit(val parent: Languages = Languages(), val id: LanguageId)

    @Resource("preview")
    class Preview(val parent: Languages = Languages(), val id: LanguageId)

    @Resource("update")
    class Update(val parent: Languages = Languages(), val id: LanguageId)
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

            call.respondHtml(HttpStatusCode.OK) {
                showLanguageDetails(call, details.id)
            }
        }
        get<Languages.New> {
            logger.info { "Add new language" }

            STORE.dispatch(CreateLanguage)

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                showLanguageEditor(call, state, state.languages.lastId)
            }
        }
        get<Languages.Delete> { delete ->
            logger.info { "Delete language ${delete.id.value}" }

            STORE.dispatch(DeleteLanguage(delete.id))

            call.respondHtml(HttpStatusCode.OK) {
                showAllLanguages(call)
            }
        }
        get<Languages.Edit> { edit ->
            logger.info { "Get editor for language ${edit.id.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val language = state.languages.get(edit.id)

                if (language != null) {
                    showLanguageEditor(call, state, language)
                } else {
                    showAllLanguages(call)
                }
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

            val formParameters = call.receiveParameters()
            val name = formParameters.getOrFail("name")

            STORE.dispatch(UpdateLanguage(update.id, name))

            call.respondHtml(HttpStatusCode.OK) {
                showLanguageDetails(call, update.id)
            }
        }
    }
}

private fun parseLanguage(id: LanguageId, parameters: Parameters): Language {
    val name = parameters.getOrFail("name")
    val origin = when (parameters["origin"]) {
        "Invented" -> {
            val inventor = CharacterId(parameters["inventor"]?.toInt() ?: 0)
            InventedLanguage(inventor)
        }

        "Evolved" -> {
            val parent = LanguageId(parameters["parent"]?.toInt() ?: 0)
            EvolvedLanguage(parent)
        }

        else -> OriginalLanguage
    }
    return Language(id, name, origin)
}

private fun HTML.showAllLanguages(call: ApplicationCall) {
    val languages = STORE.getState().languages
    val count = languages.getSize()
    val createLink = call.application.href(Languages.New(Languages()))

    simpleHtml("Languages") {
        field("Count", count.toString())
        ul {
            languages.getAll().forEach { language ->
                li {
                    val languageLink = call.application.href(Languages.Details(Languages(), language.id))
                    a(languageLink) { +language.name }
                }
            }
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showLanguageDetails(
    call: ApplicationCall,
    id: LanguageId,
) {
    val state = STORE.getState()
    val language = state.languages.get(id)

    if (language != null) {
        showLanguageDetails(call, state, language)
    } else {
        showAllLanguages(call)
    }
}

private fun HTML.showLanguageDetails(
    call: ApplicationCall,
    state: State,
    language: Language,
) {
    val backLink = call.application.href(Languages())
    val deleteLink = call.application.href(Languages.Delete(Languages(), language.id))
    val editLink = call.application.href(Languages.Edit(Languages(), language.id))
    val children = state.getChildren(language.id)

    simpleHtml("Language: ${language.name}") {
        field("Id", language.id.value.toString())
        field("Name", language.name)
        when (language.origin) {
            is EvolvedLanguage -> {
                field("Origin", "Evolved")
                field("Parent") {
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
        if (children.isNotEmpty()) {
            field("Children") {
                ul {
                    children.forEach { language ->
                        li {
                            val languageLink = call.application.href(Languages.Details(Languages(), language.id))
                            a(languageLink) { +language.name }
                        }
                    }
                }
            }
        }
        p { a(editLink) { +"Edit" } }
        p { a(deleteLink) { +"Delete" } }
        p { a(backLink) { +"Back" } }
    }
}

private fun HTML.showLanguageEditor(
    call: ApplicationCall,
    state: State,
    id: LanguageId,
) {
    val language = STORE.getState().languages.get(id)

    if (language != null) {
        showLanguageEditor(call, state, language)
    } else {
        showAllLanguages(call)
    }
}

private fun HTML.showLanguageEditor(
    call: ApplicationCall,
    state: State,
    language: Language,
) {
    val backLink = call.application.href(Languages())
    val previewLink = call.application.href(Languages.Preview(Languages(), language.id))
    val updateLink = call.application.href(Languages.Update(Languages(), language.id))

    simpleHtml("Edit Language: ${language.name}") {
        field("Id", language.id.value.toString())
        form {
            id = "editor"
            action = previewLink
            method = FormMethod.post
            label("Name") {
                b { +"Name: " }
                textInput(name = "name") {
                    value = language.name
                }
            }
            label("Origin") {
                select {
                    id = "origin"
                    name = "origin"
                    onChange = "updateEditor();"
                    option {
                        label = "Evolved"
                        value = "Evolved"
                        selected = language.origin is EvolvedLanguage
                    }
                    option {
                        label = "Invented"
                        value = "Invented"
                        selected = language.origin is InventedLanguage
                    }
                    option {
                        label = "Original"
                        value = "Original"
                        selected = language.origin is OriginalLanguage
                    }
                }
            }
            when (language.origin) {
                is EvolvedLanguage ->
                    selectEnum("Parent", "parent", state.languages.getAll().filter { l -> l.id != language.id }) { l ->
                        label = l.name
                        value = l.id.value.toString()
                        selected = language.origin.parent == l.id
                    }

                is InventedLanguage -> selectEnum(
                    "Inventor",
                    "inventor",
                    state.characters.getAll()
                ) { c ->
                    label = c.name
                    value = c.id.value.toString()
                    selected = language.origin.inventor == c.id
                }

                else -> {}
            }
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        p { a(backLink) { +"Back" } }
    }
}