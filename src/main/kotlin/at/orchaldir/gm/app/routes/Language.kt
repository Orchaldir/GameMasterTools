package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.displayOrigin
import at.orchaldir.gm.app.html.model.editLanguage
import at.orchaldir.gm.app.html.model.parseLanguage
import at.orchaldir.gm.app.html.model.showLanguage
import at.orchaldir.gm.core.action.CreateLanguage
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.LANGUAGE_TYPE
import at.orchaldir.gm.core.model.language.Language
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.selector.canDeleteLanguage
import at.orchaldir.gm.core.selector.countCharacters
import at.orchaldir.gm.core.selector.countChildren
import at.orchaldir.gm.core.selector.culture.countCultures
import at.orchaldir.gm.core.selector.item.countPeriodicals
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.magic.countSpells
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

@Resource("/$LANGUAGE_TYPE")
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
    val createLink = call.application.href(LanguageRoutes.New())

    simpleHtml("Languages") {
        field("Count", languages.size)

        table {
            tr {
                th { +"Name" }
                th { +"Origin" }
                th { +"Characters" }
                th { +"Cultures" }
                th { +"Languages" }
                th { +"Spells" }
                th { +"Periodicals" }
                th { +"Texts" }
            }
            languages.forEach { language ->
                tr {
                    td { link(call, state, language) }
                    td { displayOrigin(call, state, language) }
                    tdSkipZero(state.countCharacters(language.id))
                    tdSkipZero(state.countCultures(language.id))
                    tdSkipZero(state.countChildren(language.id))
                    tdSkipZero(state.countSpells(language.id))
                    tdSkipZero(state.countPeriodicals(language.id))
                    tdSkipZero(state.countTexts(language.id))
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

    simpleHtml("Language: ${language.name}") {
        showLanguage(call, state, language)

        action(editLink, "Edit")

        if (state.canDeleteLanguage(language.id)) {
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
    val backLink = href(call, language.id)
    val previewLink = call.application.href(LanguageRoutes.Preview(language.id))
    val updateLink = call.application.href(LanguageRoutes.Update(language.id))

    simpleHtml("Edit Language: ${language.name}") {
        formWithPreview(previewLink, updateLink, backLink) {
            editLanguage(state, language)
        }
    }
}
