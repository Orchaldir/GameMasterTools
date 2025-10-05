package at.orchaldir.gm.app.routes.culture

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.editLanguage
import at.orchaldir.gm.app.html.culture.parseLanguage
import at.orchaldir.gm.app.html.culture.showLanguage
import at.orchaldir.gm.app.html.util.showOrigin
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.All
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.New
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.language.LANGUAGE_TYPE
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.util.SortLanguage
import at.orchaldir.gm.core.model.util.SortMagicTradition
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
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$LANGUAGE_TYPE")
class LanguageRoutes : Routes<LanguageId, SortLanguage> {
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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortLanguage) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: LanguageId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: LanguageId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
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
            handleShowElement(details.id, LanguageRoutes(), HtmlBlockTag::showLanguage)
        }
        get<LanguageRoutes.New> {
            handleCreateElement(STORE.getState().getLanguageStorage()) { id ->
                LanguageRoutes.Edit(id)
            }
        }
        get<LanguageRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, LanguageRoutes.All())
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
        showSortTableLinks(call, SortLanguage.entries, LanguageRoutes())

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
