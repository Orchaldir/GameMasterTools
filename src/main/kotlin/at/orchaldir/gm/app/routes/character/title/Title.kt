package at.orchaldir.gm.app.routes.character.title

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.title.editTitle
import at.orchaldir.gm.app.html.character.title.parseTitle
import at.orchaldir.gm.app.html.character.title.showTitle
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.title.TITLE_TYPE
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.model.character.title.TitleId
import at.orchaldir.gm.core.model.util.SortTitle
import at.orchaldir.gm.core.selector.character.countCharacters
import at.orchaldir.gm.core.selector.util.sortTitles
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.table
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$TITLE_TYPE")
class TitleRoutes : Routes<TitleId> {
    @Resource("all")
    class All(
        val sort: SortTitle = SortTitle.Name,
        val parent: TitleRoutes = TitleRoutes(),
    )

    @Resource("details")
    class Details(val id: TitleId, val parent: TitleRoutes = TitleRoutes())

    @Resource("new")
    class New(val parent: TitleRoutes = TitleRoutes())

    @Resource("delete")
    class Delete(val id: TitleId, val parent: TitleRoutes = TitleRoutes())

    @Resource("edit")
    class Edit(val id: TitleId, val parent: TitleRoutes = TitleRoutes())

    @Resource("preview")
    class Preview(val id: TitleId, val parent: TitleRoutes = TitleRoutes())

    @Resource("update")
    class Update(val id: TitleId, val parent: TitleRoutes = TitleRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun delete(call: ApplicationCall, id: TitleId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: TitleId) = call.application.href(Edit(id))
}

fun Application.configureTitleRouting() {
    routing {
        get<TitleRoutes.All> { all ->
            logger.info { "Get all title" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllTitles(call, STORE.getState(), all.sort)
            }
        }
        get<TitleRoutes.Details> { details ->
            handleShowElement(details.id, TitleRoutes(), HtmlBlockTag::showTitle)
        }
        get<TitleRoutes.New> {
            handleCreateElement(STORE.getState().getTitleStorage()) { id ->
                TitleRoutes.Edit(id)
            }
        }
        get<TitleRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, TitleRoutes.All())
        }
        get<TitleRoutes.Edit> { edit ->
            logger.info { "Get editor for title ${edit.id.value}" }

            val state = STORE.getState()
            val title = state.getTitleStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTitleEditor(call, state, title)
            }
        }
        post<TitleRoutes.Preview> { preview ->
            logger.info { "Preview title ${preview.id.value}" }

            val state = STORE.getState()
            val title = parseTitle(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTitleEditor(call, state, title)
            }
        }
        post<TitleRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseTitle)
        }
    }
}

private fun HTML.showAllTitles(
    call: ApplicationCall,
    state: State,
    sort: SortTitle,
) {
    val titles = state.sortTitles(sort)
    val createLink = call.application.href(TitleRoutes.New())

    simpleHtml("Titles") {
        field("Count", titles.size)
        showSortTableLinks(call, SortTitle.entries, TitleRoutes(), TitleRoutes::All)
        table {
            tr {
                th { +"Name" }
                th { +"Text" }
                th { +"Position" }
                th { +"Separator" }
                th { +"Characters" }
            }
            titles.forEach { title ->
                tr {
                    tdLink(call, state, title)
                    tdInline(title.text.getValues()) { text ->
                        text.text
                    }
                    tdEnum(title.position)
                    tdChar(title.separator)
                    tdSkipZero(state.countCharacters(title.id))
                }
            }
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showTitleEditor(
    call: ApplicationCall,
    state: State,
    title: Title,
) {
    val backLink = href(call, title.id)
    val previewLink = call.application.href(TitleRoutes.Preview(title.id))
    val updateLink = call.application.href(TitleRoutes.Update(title.id))

    simpleHtmlEditor(title) {
        formWithPreview(previewLink, updateLink, backLink) {
            editTitle(title)
        }
    }
}
