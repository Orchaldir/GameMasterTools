package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.world.editRegion
import at.orchaldir.gm.app.html.world.parseRegion
import at.orchaldir.gm.app.html.world.showRegion
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.util.SortRegion
import at.orchaldir.gm.core.model.world.terrain.REGION_TYPE
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.selector.util.sortRegions
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

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
    override fun preview(call: ApplicationCall, id: RegionId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: RegionId) = call.application.href(Update(id))
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
            handleCreateElement(RegionRoutes(), STORE.getState().getRegionStorage())
        }
        get<RegionRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, RegionRoutes())
        }
        get<RegionRoutes.Edit> { edit ->
            handleEditElement(edit.id, RegionRoutes(), HtmlBlockTag::editRegion)
        }
        post<RegionRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, RegionRoutes(), ::parseRegion, HtmlBlockTag::editRegion)
        }
        post<RegionRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseRegion)
        }
    }
}
