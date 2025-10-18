package at.orchaldir.gm.app.routes.character.title

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.title.editTitle
import at.orchaldir.gm.app.html.character.title.parseTitle
import at.orchaldir.gm.app.html.character.title.showTitle
import at.orchaldir.gm.app.routes.*
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
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$TITLE_TYPE")
class TitleRoutes : Routes<TitleId, SortTitle> {
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
    override fun all(call: ApplicationCall, sort: SortTitle) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: TitleId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: TitleId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureTitleRouting() {
    routing {
        get<TitleRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                TitleRoutes(),
                state.sortTitles(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Text") {
                        tdInline(it.text.getValues()) { text ->
                            text.text
                        }
                    },
                    Column("Position") { tdEnum(it.position) },
                    Column("Separator") { tdChar(it.separator) },
                    createSkipZeroColumnForId("Characters", state::countCharacters),
                ),
            )
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
