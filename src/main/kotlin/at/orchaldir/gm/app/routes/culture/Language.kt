package at.orchaldir.gm.app.routes.culture

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseCharacterTemplate
import at.orchaldir.gm.app.html.culture.editLanguage
import at.orchaldir.gm.app.html.culture.parseLanguage
import at.orchaldir.gm.app.html.culture.showLanguage
import at.orchaldir.gm.app.html.util.showOrigin
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.action.DeleteLanguage
import at.orchaldir.gm.core.action.UpdateLanguage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.language.LANGUAGE_TYPE
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.util.SortLanguage
import at.orchaldir.gm.core.selector.character.countCharacters
import at.orchaldir.gm.core.selector.culture.countChildren
import at.orchaldir.gm.core.selector.culture.countCultures
import at.orchaldir.gm.core.selector.item.countTexts
import at.orchaldir.gm.core.selector.item.periodical.countPeriodicals
import at.orchaldir.gm.core.selector.magic.countSpells
import at.orchaldir.gm.core.selector.util.sortLanguages
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
    @Resource("all")
    class All(
        val sort: SortLanguage = SortLanguage.Name,
        val parent: LanguageRoutes = LanguageRoutes(),
    )

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
        get<LanguageRoutes.All> { all ->
            logger.info { "Get all languages" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllLanguages(call, STORE.getState(), all.sort)
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
            handleCreateElement(STORE.getState().getLanguageStorage()) { id ->
                LanguageRoutes.Edit(id)
            }
        }
        get<LanguageRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteLanguage(delete.id), LanguageRoutes())
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
            val language = parseLanguage(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLanguageEditor(call, state, language)
            }
        }
        post<LanguageRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseLanguage)
        }
    }
}

private fun HTML.showAllLanguages(
    call: ApplicationCall,
    state: State,
    sort: SortLanguage = SortLanguage.Name,
) {
    val languages = state.sortLanguages(sort)
    val createLink = call.application.href(LanguageRoutes.New())

    simpleHtml("Languages") {
        field("Count", languages.size)
        showSortTableLinks(call, SortLanguage.entries, LanguageRoutes(), LanguageRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Title" }
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
                    tdLink(call, state, language)
                    tdString(language.title)
                    td { showOrigin(call, state, language.origin, ::LanguageId) }
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
    val backLink = call.application.href(LanguageRoutes.All())
    val deleteLink = call.application.href(LanguageRoutes.Delete(language.id))
    val editLink = call.application.href(LanguageRoutes.Edit(language.id))

    simpleHtmlDetails(language) {
        showLanguage(call, state, language)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
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

    simpleHtmlEditor(language) {
        formWithPreview(previewLink, updateLink, backLink) {
            editLanguage(state, language)
        }
    }
}
