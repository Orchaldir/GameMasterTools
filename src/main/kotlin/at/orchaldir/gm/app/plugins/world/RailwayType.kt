package at.orchaldir.gm.app.plugins.world

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.world.parseRailwayType
import at.orchaldir.gm.core.action.CreateRailwayType
import at.orchaldir.gm.core.action.DeleteRailwayType
import at.orchaldir.gm.core.action.UpdateRailwayType
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Solid
import at.orchaldir.gm.core.model.world.railway.RailwayTypeId
import at.orchaldir.gm.core.model.world.railway.RailwayType
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.town.RAILWAY_WIDTH
import at.orchaldir.gm.visualization.town.TILE_SIZE
import at.orchaldir.gm.visualization.town.renderHorizontalRailway
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.FormMethod
import kotlinx.html.HTML
import kotlinx.html.form
import kotlinx.html.id
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/railway_types")
class RailwayTypeRoutes {
    @Resource("details")
    class Details(val id: RailwayTypeId, val parent: RailwayTypeRoutes = RailwayTypeRoutes())

    @Resource("new")
    class New(val parent: RailwayTypeRoutes = RailwayTypeRoutes())

    @Resource("delete")
    class Delete(val id: RailwayTypeId, val parent: RailwayTypeRoutes = RailwayTypeRoutes())

    @Resource("edit")
    class Edit(val id: RailwayTypeId, val parent: RailwayTypeRoutes = RailwayTypeRoutes())

    @Resource("preview")
    class Preview(val id: RailwayTypeId, val parent: RailwayTypeRoutes = RailwayTypeRoutes())

    @Resource("update")
    class Update(val id: RailwayTypeId, val parent: RailwayTypeRoutes = RailwayTypeRoutes())
}

fun Application.configureRailwayTypeRouting() {
    routing {
        get<RailwayTypeRoutes> {
            logger.info { "Get all railway types" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllRailwayTypes(call)
            }
        }
        get<RailwayTypeRoutes.Details> { details ->
            logger.info { "Get details of railway type ${details.id.value}" }

            val state = STORE.getState()
            val railway = state.getRailwayTypeStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRailwayTypeDetails(call, state, railway)
            }
        }
        get<RailwayTypeRoutes.New> {
            logger.info { "Add new railway type" }

            STORE.dispatch(CreateRailwayType)

            call.respondRedirect(
                call.application.href(
                    RailwayTypeRoutes.Edit(
                        STORE.getState().getRailwayTypeStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<RailwayTypeRoutes.Delete> { delete ->
            logger.info { "Delete railway type ${delete.id.value}" }

            STORE.dispatch(DeleteRailwayType(delete.id))

            call.respondRedirect(call.application.href(RailwayTypeRoutes()))

            STORE.getState().save()
        }
        get<RailwayTypeRoutes.Edit> { edit ->
            logger.info { "Get editor for railway type ${edit.id.value}" }

            val state = STORE.getState()
            val railway = state.getRailwayTypeStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRailwayTypeEditor(call, railway)
            }
        }
        post<RailwayTypeRoutes.Preview> { preview ->
            logger.info { "Preview railway type ${preview.id.value}" }

            val type = parseRailwayType(preview.id, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                showRailwayTypeEditor(call, type)
            }
        }
        post<RailwayTypeRoutes.Update> { update ->
            logger.info { "Update railway type ${update.id.value}" }

            val type = parseRailwayType(update.id, call.receiveParameters())

            STORE.dispatch(UpdateRailwayType(type))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllRailwayTypes(call: ApplicationCall) {
    val railways = STORE.getState().getRailwayTypeStorage().getAll().sortedBy { it.name }
    val count = railways.size
    val createLink = call.application.href(RailwayTypeRoutes.New())

    simpleHtml("Railway Types") {
        field("Count", count.toString())
        showList(railways) { type ->
            link(call, type)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showRailwayTypeDetails(
    call: ApplicationCall,
    state: State,
    type: RailwayType,
) {
    val backLink = call.application.href(RailwayTypeRoutes())
    val deleteLink = call.application.href(RailwayTypeRoutes.Delete(type.id))
    val editLink = call.application.href(RailwayTypeRoutes.Edit(type.id))

    simpleHtml("Railway Type: ${type.name}") {
        split({
            field("Id", type.id.value.toString())
            field("Name", type.name)
            field("Color", type.color.toString())
            action(editLink, "Edit")
            if (state.canDelete(type.id)) {
                action(deleteLink, "Delete")
            }
            back(backLink)
        }, {
            svg(visualizeRailwayType(type), 90)
        })
    }
}

private fun HTML.showRailwayTypeEditor(
    call: ApplicationCall,
    type: RailwayType,
) {
    val backLink = href(call, type.id)
    val previewLink = call.application.href(RailwayTypeRoutes.Preview(type.id))
    val updateLink = call.application.href(RailwayTypeRoutes.Update(type.id))

    simpleHtml("Edit Railway Type: ${type.name}") {
        split({
            field("Id", type.id.value.toString())
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectName(type.name)
                selectColor("Color", COLOR, Color.entries, type.color)
                button("Update", updateLink)
            }
            back(backLink)
        }, {
            svg(visualizeRailwayType(type), 90)
        })
    }
}

private fun visualizeRailwayType(
    railwayType: RailwayType,
): Svg {
    val size = Size2d.square(TILE_SIZE)
    val builder = SvgBuilder(size)
    val aabb = AABB(size)

    renderHorizontalRailway(builder.getLayer(), aabb, railwayType.color, RAILWAY_WIDTH)

    return builder.finish()
}