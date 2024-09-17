package at.orchaldir.gm.app.plugins.world.town

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.world.parseTown
import at.orchaldir.gm.core.action.CreateTown
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.visualization.town.visualizeTown
import io.ktor.http.*
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

fun Application.configureTerrainRouting() {
    routing {
        get<TownRoutes.Edit> { edit ->
            logger.info { "Get the terrain editor for town ${edit.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, town)
            }
        }
        post<TownRoutes.Update> { update ->
            logger.info { "Update the terrain for town ${update.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(update.id)

            STORE.dispatch(UpdateTown(town))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, town)
            }
        }
    }
}

private fun HTML.showTerrainEditor(
    call: ApplicationCall,
    town: Town,
) {
    val backLink = href(call, town.id)
    val updateLink = call.application.href(TownRoutes.Update(town.id))

    simpleHtml("Edit Terrain of Town ${town.name}") {
        split({
            form {
                selectName(town.name)
                p {
                    submitInput {
                        value = "Update"
                        formAction = updateLink
                        formMethod = InputFormMethod.post
                    }
                }
            }
            back(backLink)
        }, {
            svg(visualizeTown(town), 90)
        })
    }
}
