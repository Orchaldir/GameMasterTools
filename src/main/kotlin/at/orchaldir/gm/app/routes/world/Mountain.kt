package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.world.parseMountain
import at.orchaldir.gm.core.action.CreateMountain
import at.orchaldir.gm.core.action.DeleteMountain
import at.orchaldir.gm.core.action.UpdateMountain
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.MOUNTAIN_TYPE
import at.orchaldir.gm.core.model.world.terrain.Mountain
import at.orchaldir.gm.core.model.world.terrain.MountainId
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.core.selector.world.getTowns
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

@Resource("/$MOUNTAIN_TYPE")
class MountainRoutes {
    @Resource("details")
    class Details(val id: MountainId, val parent: MountainRoutes = MountainRoutes())

    @Resource("new")
    class New(val parent: MountainRoutes = MountainRoutes())

    @Resource("delete")
    class Delete(val id: MountainId, val parent: MountainRoutes = MountainRoutes())

    @Resource("edit")
    class Edit(val id: MountainId, val parent: MountainRoutes = MountainRoutes())

    @Resource("update")
    class Update(val id: MountainId, val parent: MountainRoutes = MountainRoutes())
}

fun Application.configureMountainRouting() {
    routing {
        get<MountainRoutes> {
            logger.info { "Get all mountains" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllMountains(call, STORE.getState())
            }
        }
        get<MountainRoutes.Details> { details ->
            logger.info { "Get details of mountain ${details.id.value}" }

            val state = STORE.getState()
            val mountain = state.getMountainStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMountainDetails(call, state, mountain)
            }
        }
        get<MountainRoutes.New> {
            logger.info { "Add new mountain" }

            STORE.dispatch(CreateMountain)

            call.respondRedirect(
                call.application.href(
                    MountainRoutes.Edit(
                        STORE.getState().getMountainStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<MountainRoutes.Delete> { delete ->
            logger.info { "Delete mountain ${delete.id.value}" }

            STORE.dispatch(DeleteMountain(delete.id))

            call.respondRedirect(call.application.href(MountainRoutes()))

            STORE.getState().save()
        }
        get<MountainRoutes.Edit> { edit ->
            logger.info { "Get editor for mountain ${edit.id.value}" }

            val state = STORE.getState()
            val mountain = state.getMountainStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMountainEditor(call, state, mountain)
            }
        }
        post<MountainRoutes.Update> { update ->
            logger.info { "Update mountain ${update.id.value}" }

            val mountain = parseMountain(update.id, call.receiveParameters())

            STORE.dispatch(UpdateMountain(mountain))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllMountains(
    call: ApplicationCall,
    state: State,
) {
    val mountains = state.getMountainStorage().getAll().sortedBy { it.name }
    val createLink = call.application.href(MountainRoutes.New())

    simpleHtml("Mountains") {
        field("Count", mountains.size)

        table {
            tr {
                th { +"Name" }
                th { +"Resources" }
            }
            mountains.forEach { mountain ->
                tr {
                    td { link(call, mountain) }
                    td {
                        state.getMaterialStorage().get(mountain.resources)
                            .sortedBy { it.name }
                            .map { link(call, state, it) }
                    }
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showMountainDetails(
    call: ApplicationCall,
    state: State,
    mountain: Mountain,
) {
    val backLink = call.application.href(MountainRoutes())
    val deleteLink = call.application.href(MountainRoutes.Delete(mountain.id))
    val editLink = call.application.href(MountainRoutes.Edit(mountain.id))

    simpleHtml("Mountain: ${mountain.name}") {
        field("Name", mountain.name)
        showList("Resources", mountain.resources) { resource ->
            link(call, state, resource)
        }
        showList("Towns", state.getTowns(mountain.id)) { town ->
            link(call, state, town)
        }

        action(editLink, "Edit")

        if (state.canDelete(mountain.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showMountainEditor(
    call: ApplicationCall,
    state: State,
    mountain: Mountain,
) {
    val materials = state.getMaterialStorage().getAll().sortedBy { it.name }
    val backLink = href(call, mountain.id)
    val updateLink = call.application.href(MountainRoutes.Update(mountain.id))

    simpleHtml("Edit Mountain: ${mountain.name}") {
        form {
            selectName(mountain.name)
            selectElements(state, "Resources", MATERIAL, materials, mountain.resources)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
