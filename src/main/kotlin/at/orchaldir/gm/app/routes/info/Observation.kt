package at.orchaldir.gm.app.routes.info

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.info.editObservation
import at.orchaldir.gm.app.html.info.parseObservation
import at.orchaldir.gm.app.html.info.showObservation
import at.orchaldir.gm.app.html.util.showPosition
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.core.action.CreateObservation
import at.orchaldir.gm.core.action.DeleteObservation
import at.orchaldir.gm.core.action.UpdateObservation
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.info.observation.Observation
import at.orchaldir.gm.core.model.info.observation.ObservationId
import at.orchaldir.gm.core.model.util.SortObservation
import at.orchaldir.gm.core.model.world.WORLD_TYPE
import at.orchaldir.gm.core.selector.util.getMoonsOf
import at.orchaldir.gm.core.selector.util.getRegionsIn
import at.orchaldir.gm.core.selector.util.sortObservations
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
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$WORLD_TYPE")
class ObservationRoutes {
    @Resource("all")
    class All(
        val sort: SortObservation = SortObservation.Name,
        val parent: ObservationRoutes = ObservationRoutes(),
    )

    @Resource("details")
    class Details(val id: ObservationId, val parent: ObservationRoutes = ObservationRoutes())

    @Resource("new")
    class New(val parent: ObservationRoutes = ObservationRoutes())

    @Resource("delete")
    class Delete(val id: ObservationId, val parent: ObservationRoutes = ObservationRoutes())

    @Resource("edit")
    class Edit(val id: ObservationId, val parent: ObservationRoutes = ObservationRoutes())

    @Resource("preview")
    class Preview(val id: ObservationId, val parent: ObservationRoutes = ObservationRoutes())

    @Resource("update")
    class Update(val id: ObservationId, val parent: ObservationRoutes = ObservationRoutes())
}

fun Application.configureObservationRouting() {
    routing {
        get<ObservationRoutes.All> { all ->
            logger.info { "Get all observations" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllObservations(call, STORE.getState(), all.sort)
            }
        }
        get<ObservationRoutes.Details> { details ->
            logger.info { "Get details of observation ${details.id.value}" }

            val state = STORE.getState()
            val observation = state.getObservationStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showObservationDetails(call, state, observation)
            }
        }
        get<ObservationRoutes.New> {
            logger.info { "Add new observation" }

            STORE.dispatch(CreateObservation)

            call.respondRedirect(call.application.href(ObservationRoutes.Edit(STORE.getState().getObservationStorage().lastId)))

            STORE.getState().save()
        }
        get<ObservationRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteObservation(delete.id), ObservationRoutes())
        }
        get<ObservationRoutes.Edit> { edit ->
            logger.info { "Get editor for observation ${edit.id.value}" }

            val state = STORE.getState()
            val observation = state.getObservationStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showObservationEditor(call, state, observation)
            }
        }
        post<ObservationRoutes.Preview> { preview ->
            logger.info { "Get preview for ${preview.id.print()}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val region = parseObservation(formParameters, state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showObservationEditor(call, state, region)
            }
        }
        post<ObservationRoutes.Update> { update ->
            logger.info { "Update observation ${update.id.value}" }

            val observation = parseObservation(call.receiveParameters(), STORE.getState(), update.id)

            STORE.dispatch(UpdateObservation(observation))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllObservations(
    call: ApplicationCall,
    state: State,
    sort: SortObservation = SortObservation.Name,
) {
    val observations = state.sortObservations(sort)
    val createLink = call.application.href(ObservationRoutes.New())

    simpleHtml("Observations") {
        field("Count", observations.size)
        showSortTableLinks(call, SortObservation.entries, ObservationRoutes(), ObservationRoutes::All)

        table {
            tr {
                th { +"Name" }
            }
            observations.forEach { observation ->
                tr {
                    tdLink(call, state, observation)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showObservationDetails(
    call: ApplicationCall,
    state: State,
    observation: Observation,
) {
    val backLink = call.application.href(ObservationRoutes.All())
    val deleteLink = call.application.href(ObservationRoutes.Delete(observation.id))
    val editLink = call.application.href(ObservationRoutes.Edit(observation.id))

    simpleHtmlDetails(observation) {
        showObservation(call, state, observation)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showObservationEditor(
    call: ApplicationCall,
    state: State,
    observation: Observation,
) {
    val backLink = href(call, observation.id)
    val previewLink = call.application.href(ObservationRoutes.Preview(observation.id))
    val updateLink = call.application.href(ObservationRoutes.Update(observation.id))

    simpleHtmlEditor(observation) {
        formWithPreview(previewLink, updateLink, backLink) {
            editObservation(state, observation)
        }
    }
}
