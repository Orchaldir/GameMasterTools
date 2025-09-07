package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.world.parseRiver
import at.orchaldir.gm.core.action.CreateRiver
import at.orchaldir.gm.core.action.DeleteRiver
import at.orchaldir.gm.core.action.UpdateRiver
import at.orchaldir.gm.core.model.CannotDeleteException
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.RIVER_TYPE
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.selector.world.canDeleteRiver
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
import kotlinx.html.HTML
import kotlinx.html.form
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$RIVER_TYPE")
class RiverRoutes {
    @Resource("details")
    class Details(val id: RiverId, val parent: RiverRoutes = RiverRoutes())

    @Resource("new")
    class New(val parent: RiverRoutes = RiverRoutes())

    @Resource("delete")
    class Delete(val id: RiverId, val parent: RiverRoutes = RiverRoutes())

    @Resource("edit")
    class Edit(val id: RiverId, val parent: RiverRoutes = RiverRoutes())

    @Resource("update")
    class Update(val id: RiverId, val parent: RiverRoutes = RiverRoutes())
}

fun Application.configureRiverRouting() {
    routing {
        get<RiverRoutes> {
            logger.info { "Get all rivers" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllRivers(call)
            }
        }
        get<RiverRoutes.Details> { details ->
            logger.info { "Get details of river ${details.id.value}" }

            val state = STORE.getState()
            val river = state.getRiverStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRiverDetails(call, state, river)
            }
        }
        get<RiverRoutes.New> {
            logger.info { "Add new river" }

            STORE.dispatch(CreateRiver)

            call.respondRedirect(call.application.href(RiverRoutes.Edit(STORE.getState().getRiverStorage().lastId)))

            STORE.getState().save()
        }
        get<RiverRoutes.Delete> { delete ->
            logger.info { "Delete river ${delete.id.value}" }

            try {
                STORE.dispatch(DeleteRiver(delete.id))

                call.respondRedirect(call.application.href(RiverRoutes()))

                STORE.getState().save()
            } catch (e: CannotDeleteException) {
                logger.warn { e.message }
                call.respondHtml(HttpStatusCode.OK) {
                    showDeleteResult(call, STORE.getState(), e.result)
                }
            }
        }
        get<RiverRoutes.Edit> { edit ->
            logger.info { "Get editor for river ${edit.id.value}" }

            val state = STORE.getState()
            val river = state.getRiverStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRiverEditor(call, river)
            }
        }
        post<RiverRoutes.Update> { update ->
            logger.info { "Update river ${update.id.value}" }

            val river = parseRiver(update.id, call.receiveParameters())

            STORE.dispatch(UpdateRiver(river))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllRivers(call: ApplicationCall) {
    val rivers = STORE.getState().getRiverStorage().getAll().sortedBy { it.name.text }
    val createLink = call.application.href(RiverRoutes.New())

    simpleHtml("Rivers") {
        field("Count", rivers.size)
        showList(rivers) { nameList ->
            link(call, nameList)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showRiverDetails(
    call: ApplicationCall,
    state: State,
    river: River,
) {
    val backLink = call.application.href(RiverRoutes())
    val deleteLink = call.application.href(RiverRoutes.Delete(river.id))
    val editLink = call.application.href(RiverRoutes.Edit(river.id))

    simpleHtmlDetails(river) {
        fieldName(river.name)
        fieldList(call, state, state.getTowns(river.id))

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showRiverEditor(
    call: ApplicationCall,
    river: River,
) {
    val backLink = href(call, river.id)
    val updateLink = call.application.href(RiverRoutes.Update(river.id))

    simpleHtmlEditor(river) {
        form {
            selectName(river.name)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
