package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.world.*
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.util.SortPlane
import at.orchaldir.gm.core.model.world.plane.IndependentPlane
import at.orchaldir.gm.core.model.world.plane.PLANE_TYPE
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.util.sortPlanes
import at.orchaldir.gm.core.selector.world.getPlanarAlignment
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$PLANE_TYPE")
class PlaneRoutes : Routes<PlaneId, SortPlane> {
    @Resource("all")
    class All(
        val sort: SortPlane = SortPlane.Name,
        val parent: PlaneRoutes = PlaneRoutes(),
    )

    @Resource("details")
    class Details(val id: PlaneId, val parent: PlaneRoutes = PlaneRoutes())

    @Resource("new")
    class New(val parent: PlaneRoutes = PlaneRoutes())

    @Resource("delete")
    class Delete(val id: PlaneId, val parent: PlaneRoutes = PlaneRoutes())

    @Resource("edit")
    class Edit(val id: PlaneId, val parent: PlaneRoutes = PlaneRoutes())

    @Resource("preview")
    class Preview(val id: PlaneId, val parent: PlaneRoutes = PlaneRoutes())

    @Resource("update")
    class Update(val id: PlaneId, val parent: PlaneRoutes = PlaneRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortPlane) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: PlaneId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: PlaneId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: PlaneId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: PlaneId) = call.application.href(Edit(id))
}

fun Application.configurePlaneRouting() {
    routing {
        get<PlaneRoutes.All> { all ->
            val state = STORE.getState()
            val day = state.getCurrentDate()

            handleShowAllElements(
                PlaneRoutes(),
                state.sortPlanes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Title") { tdString(it.title) },
                    tdColumn("Purpose") {
                        if (it.purpose is IndependentPlane) {
                            displayPlaneAlignmentPattern(it.purpose.pattern)
                        }
                    },
                    tdColumn("Alignment") { displayPlanePurpose(call, state, it.purpose, false) },
                    Column("Current") { tdOptionalEnum(state.getPlanarAlignment(it, day)) },
                    Column("Languages") { tdInlineIds(call, state, it.languages) },
                ),
            )
        }
        get<PlaneRoutes.Details> { details ->
            handleShowElement(details.id, PlaneRoutes(), HtmlBlockTag::showPlane)
        }
        get<PlaneRoutes.New> {
            handleCreateElement(STORE.getState().getPlaneStorage()) { id ->
                PlaneRoutes.Edit(id)
            }
        }
        get<PlaneRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, PlaneRoutes.All())
        }
        get<PlaneRoutes.Edit> { edit ->
            handleEditElement(edit.id, PlaneRoutes(), HtmlBlockTag::editPlane)
        }
        post<PlaneRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, PlaneRoutes(), ::parsePlane, HtmlBlockTag::editPlane)
        }
        post<PlaneRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parsePlane)
        }
    }
}
