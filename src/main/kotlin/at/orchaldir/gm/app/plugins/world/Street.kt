package at.orchaldir.gm.app.plugins.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.world.parseStreet
import at.orchaldir.gm.core.action.CreateStreet
import at.orchaldir.gm.core.action.DeleteStreet
import at.orchaldir.gm.core.action.UpdateStreet
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
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

@Resource("/streets")
class StreetRoutes {
    @Resource("details")
    class Details(val id: StreetId, val parent: StreetRoutes = StreetRoutes())

    @Resource("new")
    class New(val parent: StreetRoutes = StreetRoutes())

    @Resource("delete")
    class Delete(val id: StreetId, val parent: StreetRoutes = StreetRoutes())

    @Resource("edit")
    class Edit(val id: StreetId, val parent: StreetRoutes = StreetRoutes())

    @Resource("update")
    class Update(val id: StreetId, val parent: StreetRoutes = StreetRoutes())
}

fun Application.configureStreetRouting() {
    routing {
        get<StreetRoutes> {
            logger.info { "Get all streets" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllStreets(call)
            }
        }
        get<StreetRoutes.Details> { details ->
            logger.info { "Get details of street ${details.id.value}" }

            val state = STORE.getState()
            val street = state.getStreetStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetDetails(call, state, street)
            }
        }
        get<StreetRoutes.New> {
            logger.info { "Add new street" }

            STORE.dispatch(CreateStreet)

            call.respondRedirect(call.application.href(StreetRoutes.Edit(STORE.getState().getStreetStorage().lastId)))

            STORE.getState().save()
        }
        get<StreetRoutes.Delete> { delete ->
            logger.info { "Delete street ${delete.id.value}" }

            STORE.dispatch(DeleteStreet(delete.id))

            call.respondRedirect(call.application.href(StreetRoutes()))

            STORE.getState().save()
        }
        get<StreetRoutes.Edit> { edit ->
            logger.info { "Get editor for street ${edit.id.value}" }

            val state = STORE.getState()
            val street = state.getStreetStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetEditor(call, street)
            }
        }
        post<StreetRoutes.Update> { update ->
            logger.info { "Update street ${update.id.value}" }

            val street = parseStreet(update.id, call.receiveParameters())

            STORE.dispatch(UpdateStreet(street))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllStreets(call: ApplicationCall) {
    val streets = STORE.getState().getStreetStorage().getAll().sortedBy { it.name }
    val count = streets.size
    val createLink = call.application.href(StreetRoutes.New())

    simpleHtml("Streets") {
        field("Count", count.toString())
        showList(streets) { nameList ->
            link(call, nameList)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showStreetDetails(
    call: ApplicationCall,
    state: State,
    street: Street,
) {
    val backLink = call.application.href(StreetRoutes())
    val deleteLink = call.application.href(StreetRoutes.Delete(street.id))
    val editLink = call.application.href(StreetRoutes.Edit(street.id))

    simpleHtml("Street: ${street.name}") {
        field("Id", street.id.value.toString())
        field("Name", street.name)
        showList("Towns", state.getTowns(street.id)) { town ->
            link(call, town)
        }
        action(editLink, "Edit")
        if (state.canDelete(street.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showStreetEditor(
    call: ApplicationCall,
    street: Street,
) {
    val backLink = href(call, street.id)
    val updateLink = call.application.href(StreetRoutes.Update(street.id))

    simpleHtml("Edit Street: ${street.name}") {
        field("Id", street.id.value.toString())
        form {
            selectName(street.name)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
