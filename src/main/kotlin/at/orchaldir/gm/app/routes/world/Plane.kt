package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.world.*
import at.orchaldir.gm.core.action.CreatePlane
import at.orchaldir.gm.core.action.DeletePlane
import at.orchaldir.gm.core.action.UpdatePlane
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortPlane
import at.orchaldir.gm.core.model.world.plane.IndependentPlane
import at.orchaldir.gm.core.model.world.plane.PLANE_TYPE
import at.orchaldir.gm.core.model.world.plane.Plane
import at.orchaldir.gm.core.model.world.plane.PlaneId
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.util.sortPlanes
import at.orchaldir.gm.core.selector.world.canDeletePlane
import at.orchaldir.gm.core.selector.world.getPlanarAlignment
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

@Resource("/$PLANE_TYPE")
class PlaneRoutes {
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
}

fun Application.configurePlaneRouting() {
    routing {
        get<PlaneRoutes.All> { all ->
            logger.info { "Get all planes" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllPlanes(call, STORE.getState(), all.sort)
            }
        }
        get<PlaneRoutes.Details> { details ->
            logger.info { "Get details of plane ${details.id.value}" }

            val state = STORE.getState()
            val plane = state.getPlaneStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPlaneDetails(call, state, plane)
            }
        }
        get<PlaneRoutes.New> {
            logger.info { "Add new plane" }

            STORE.dispatch(CreatePlane)

            call.respondRedirect(call.application.href(PlaneRoutes.Edit(STORE.getState().getPlaneStorage().lastId)))

            STORE.getState().save()
        }
        get<PlaneRoutes.Delete> { delete ->
            logger.info { "Delete plane ${delete.id.value}" }

            STORE.dispatch(DeletePlane(delete.id))

            call.respondRedirect(call.application.href(PlaneRoutes()))

            STORE.getState().save()
        }
        get<PlaneRoutes.Edit> { edit ->
            logger.info { "Get editor for plane ${edit.id.value}" }

            val state = STORE.getState()
            val plane = state.getPlaneStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPlaneEditor(call, state, plane)
            }
        }
        post<PlaneRoutes.Preview> { preview ->
            logger.info { "Get preview for plane ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val plane = parsePlane(formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showPlaneEditor(call, state, plane)
            }
        }
        post<PlaneRoutes.Update> { update ->
            logger.info { "Update plane ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val plane = parsePlane(formParameters, update.id)

            STORE.dispatch(UpdatePlane(plane))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllPlanes(
    call: ApplicationCall,
    state: State,
    sort: SortPlane,
) {
    val day = state.getCurrentDate()
    val planes = state.sortPlanes(sort)
    val createLink = call.application.href(PlaneRoutes.New())

    simpleHtml("Planes") {
        field("Count", planes.size)
        showSortTableLinks(call, SortPlane.entries, PlaneRoutes(), PlaneRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Title" }
                th { +"Purpose" }
                th { +"Alignment" }
                th { +"Current" }
            }
            planes.forEach { plane ->
                tr {
                    tdLink(call, state, plane)
                    tdString(plane.title)
                    td { displayPlanePurpose(call, state, plane.purpose, false) }
                    td {
                        if (plane.purpose is IndependentPlane) {
                            displayPlaneAlignmentPattern(plane.purpose.pattern)
                        }
                    }
                    tdOptionalEnum(state.getPlanarAlignment(plane, day))
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showPlaneDetails(
    call: ApplicationCall,
    state: State,
    plane: Plane,
) {
    val backLink = call.application.href(PlaneRoutes.All())
    val deleteLink = call.application.href(PlaneRoutes.Delete(plane.id))
    val editLink = call.application.href(PlaneRoutes.Edit(plane.id))

    simpleHtmlDetails(plane) {
        showPlane(call, state, plane)

        action(editLink, "Edit")

        if (state.canDeletePlane(plane.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showPlaneEditor(
    call: ApplicationCall,
    state: State,
    plane: Plane,
) {
    val backLink = href(call, plane.id)
    val previewLink = call.application.href(PlaneRoutes.Preview(plane.id))
    val updateLink = call.application.href(PlaneRoutes.Update(plane.id))

    simpleHtmlEditor(plane) {
        formWithPreview(previewLink, updateLink, backLink) {
            editPlane(state, plane)
        }
    }
}

