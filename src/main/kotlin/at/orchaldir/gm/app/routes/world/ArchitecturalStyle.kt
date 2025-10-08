package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.html.world.editArchitecturalStyle
import at.orchaldir.gm.app.html.world.parseArchitecturalStyle
import at.orchaldir.gm.app.html.world.showArchitecturalStyle
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.All
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.New
import at.orchaldir.gm.app.routes.utls.QuoteRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortArchitecturalStyle
import at.orchaldir.gm.core.model.util.SortArchitecturalStyle.Name
import at.orchaldir.gm.core.model.util.SortMagicTradition
import at.orchaldir.gm.core.model.world.building.ARCHITECTURAL_STYLE_TYPE
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyle
import at.orchaldir.gm.core.model.world.building.ArchitecturalStyleId
import at.orchaldir.gm.core.selector.util.sortArchitecturalStyles
import at.orchaldir.gm.core.selector.util.sortQuotes
import at.orchaldir.gm.core.selector.world.getBuildings
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

@Resource("/$ARCHITECTURAL_STYLE_TYPE")
class ArchitecturalStyleRoutes : Routes<ArchitecturalStyleId, SortArchitecturalStyle> {
    @Resource("all")
    class All(
        val sort: SortArchitecturalStyle = Name,
        val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes(),
    )

    @Resource("details")
    class Details(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("new")
    class New(val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("delete")
    class Delete(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("edit")
    class Edit(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("preview")
    class Preview(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    @Resource("update")
    class Update(val id: ArchitecturalStyleId, val parent: ArchitecturalStyleRoutes = ArchitecturalStyleRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortArchitecturalStyle) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: ArchitecturalStyleId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: ArchitecturalStyleId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureArchitecturalStyleRouting() {
    routing {
        get<ArchitecturalStyleRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                ArchitecturalStyleRoutes(),
                state.sortArchitecturalStyles(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    createEndDateColumn(call, state),
                    Column("Revival of") { tdLink(call, state, it.revival) },
                    Column("Buildings") { tdSkipZero(state.getBuildings(it.id)) },
                ),
            )
        }
        get<ArchitecturalStyleRoutes.Details> { details ->
            handleShowElement(details.id, ArchitecturalStyleRoutes(), HtmlBlockTag::showArchitecturalStyle)
        }
        get<ArchitecturalStyleRoutes.New> {
            handleCreateElement(STORE.getState().getArchitecturalStyleStorage()) { id ->
                ArchitecturalStyleRoutes.Edit(id)
            }
        }
        get<ArchitecturalStyleRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, ArchitecturalStyleRoutes.All())
        }
        get<ArchitecturalStyleRoutes.Edit> { edit ->
            logger.info { "Get editor for architectural style ${edit.id.value}" }

            val state = STORE.getState()
            val style = state.getArchitecturalStyleStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showArchitecturalStyleEditor(call, state, style)
            }
        }
        post<ArchitecturalStyleRoutes.Preview> { preview ->
            logger.info { "Get preview for architectural style ${preview.id.value}" }

            val state = STORE.getState()
            val style = parseArchitecturalStyle(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showArchitecturalStyleEditor(call, state, style)
            }
        }
        post<ArchitecturalStyleRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseArchitecturalStyle)
        }
    }
}

private fun HTML.showArchitecturalStyleEditor(
    call: ApplicationCall,
    state: State,
    style: ArchitecturalStyle,
) {
    val backLink = href(call, style.id)
    val previewLink = call.application.href(ArchitecturalStyleRoutes.Preview(style.id))
    val updateLink = call.application.href(ArchitecturalStyleRoutes.Update(style.id))

    simpleHtmlEditor(style) {
        formWithPreview(previewLink, updateLink, backLink) {
            editArchitecturalStyle(state, style)
        }
    }
}
