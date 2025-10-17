package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.util.showPosition
import at.orchaldir.gm.app.html.world.editRegion
import at.orchaldir.gm.app.html.world.parseRegion
import at.orchaldir.gm.app.html.world.showRegion
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowAllElements
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.All
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.New
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortMagicTradition
import at.orchaldir.gm.core.model.util.SortRegion
import at.orchaldir.gm.core.model.world.terrain.REGION_TYPE
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.selector.util.sortMoons
import at.orchaldir.gm.core.selector.util.sortRegions
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
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$REGION_TYPE")
class RegionRoutes : Routes<RegionId, SortRegion> {
    @Resource("all")
    class All(
        val sort: SortRegion = SortRegion.Name,
        val parent: RegionRoutes = RegionRoutes(),
    )

    @Resource("details")
    class Details(val id: RegionId, val parent: RegionRoutes = RegionRoutes())

    @Resource("new")
    class New(val parent: RegionRoutes = RegionRoutes())

    @Resource("delete")
    class Delete(val id: RegionId, val parent: RegionRoutes = RegionRoutes())

    @Resource("edit")
    class Edit(val id: RegionId, val parent: RegionRoutes = RegionRoutes())

    @Resource("preview")
    class Preview(val id: RegionId, val parent: RegionRoutes = RegionRoutes())

    @Resource("update")
    class Update(val id: RegionId, val parent: RegionRoutes = RegionRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortRegion) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: RegionId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: RegionId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureRegionRouting() {
    routing {
        get<RegionRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                RegionRoutes(),
                state.sortRegions(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Type") { tdEnum(it.data.getType()) },
                    createPositionColumn(call, state),
                    Column("Resources") { tdInlineIds(call, state, it.resources) },
                ),
            )
        }
        get<RegionRoutes.Details> { details ->
            handleShowElement(details.id, RegionRoutes(), HtmlBlockTag::showRegion)
        }
        get<RegionRoutes.New> {
            handleCreateElement(STORE.getState().getRegionStorage(), RegionRoutes::Edit)
        }
        get<RegionRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, RegionRoutes.All())
        }
        get<RegionRoutes.Edit> { edit ->
            logger.info { "Get editor for region ${edit.id.value}" }

            val state = STORE.getState()
            val region = state.getRegionStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRegionEditor(call, state, region)
            }
        }
        post<RegionRoutes.Preview> { preview ->
            logger.info { "Get preview for region ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val region = parseRegion(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRegionEditor(call, state, region)
            }
        }
        post<RegionRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseRegion)
        }
    }
}

private fun HTML.showRegionEditor(
    call: ApplicationCall,
    state: State,
    region: Region,
) {
    val backLink = href(call, region.id)
    val previewLink = call.application.href(RegionRoutes.Preview(region.id))
    val updateLink = call.application.href(RegionRoutes.Update(region.id))

    simpleHtmlEditor(region) {
        formWithPreview(previewLink, updateLink, backLink) {
            editRegion(state, region)
        }
    }
}
