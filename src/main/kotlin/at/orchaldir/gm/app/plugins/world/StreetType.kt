package at.orchaldir.gm.app.plugins.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.world.parseStreetType
import at.orchaldir.gm.core.action.CreateStreetType
import at.orchaldir.gm.core.action.DeleteStreetType
import at.orchaldir.gm.core.action.UpdateStreetType
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.StreetType
import at.orchaldir.gm.core.model.world.street.StreetTypeId
import at.orchaldir.gm.core.selector.world.canDelete
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

@Resource("/street_types")
class StreetTypeRoutes {
    @Resource("details")
    class Details(val id: StreetTypeId, val parent: StreetTypeRoutes = StreetTypeRoutes())

    @Resource("new")
    class New(val parent: StreetTypeRoutes = StreetTypeRoutes())

    @Resource("delete")
    class Delete(val id: StreetTypeId, val parent: StreetTypeRoutes = StreetTypeRoutes())

    @Resource("edit")
    class Edit(val id: StreetTypeId, val parent: StreetTypeRoutes = StreetTypeRoutes())

    @Resource("update")
    class Update(val id: StreetTypeId, val parent: StreetTypeRoutes = StreetTypeRoutes())
}

fun Application.configureStreetTypeRouting() {
    routing {
        get<StreetTypeRoutes> {
            logger.info { "Get all street types" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllStreetTypes(call)
            }
        }
        get<StreetTypeRoutes.Details> { details ->
            logger.info { "Get details of street type ${details.id.value}" }

            val state = STORE.getState()
            val street = state.getStreetTypeStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetTypeDetails(call, state, street)
            }
        }
        get<StreetTypeRoutes.New> {
            logger.info { "Add new street type" }

            STORE.dispatch(CreateStreetType)

            call.respondRedirect(
                call.application.href(
                    StreetTypeRoutes.Edit(
                        STORE.getState().getStreetTypeStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<StreetTypeRoutes.Delete> { delete ->
            logger.info { "Delete street type ${delete.id.value}" }

            STORE.dispatch(DeleteStreetType(delete.id))

            call.respondRedirect(call.application.href(StreetTypeRoutes()))

            STORE.getState().save()
        }
        get<StreetTypeRoutes.Edit> { edit ->
            logger.info { "Get editor for street type ${edit.id.value}" }

            val state = STORE.getState()
            val street = state.getStreetTypeStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetTypeEditor(call, street)
            }
        }
        post<StreetTypeRoutes.Update> { update ->
            logger.info { "Update street type ${update.id.value}" }

            val street = parseStreetType(update.id, call.receiveParameters())

            STORE.dispatch(UpdateStreetType(street))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllStreetTypes(call: ApplicationCall) {
    val streets = STORE.getState().getStreetTypeStorage().getAll().sortedBy { it.name }
    val count = streets.size
    val createLink = call.application.href(StreetTypeRoutes.New())

    simpleHtml("Street Types") {
        field("Count", count.toString())
        showList(streets) { type ->
            link(call, type)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showStreetTypeDetails(
    call: ApplicationCall,
    state: State,
    street: StreetType,
) {
    val backLink = call.application.href(StreetTypeRoutes())
    val deleteLink = call.application.href(StreetTypeRoutes.Delete(street.id))
    val editLink = call.application.href(StreetTypeRoutes.Edit(street.id))

    simpleHtml("Street Type: ${street.name}") {
        field("Id", street.id.value.toString())
        field("Name", street.name)
        action(editLink, "Edit")
        if (state.canDelete(street.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showStreetTypeEditor(
    call: ApplicationCall,
    street: StreetType,
) {
    val backLink = href(call, street.id)
    val updateLink = call.application.href(StreetTypeRoutes.Update(street.id))

    simpleHtml("Edit Street Type: ${street.name}") {
        field("Id", street.id.value.toString())
        form {
            selectName(street.name)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
