package at.orchaldir.gm.app.plugins.world.town

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.STREET
import at.orchaldir.gm.app.TERRAIN
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.plugins.world.StreetRoutes
import at.orchaldir.gm.core.action.AddStreetTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.visualization.town.visualizeTown
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.html.FormMethod
import kotlinx.html.HTML
import kotlinx.html.form
import kotlinx.html.id
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureStreetEditorRouting() {
    routing {
        get<TownRoutes.StreetRoutes.Edit> { edit ->
            logger.info { "Get the street editor for town ${edit.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(edit.id)
            val params = call.receiveParameters()
            val streetId: Int = parseInt(params, TERRAIN, 0)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetEditor(call, state, town, streetId)
            }
        }
        get<TownRoutes.StreetRoutes.Add> { add ->
            logger.info { "Set tile ${add.tileIndex} to street ${add.streetId} for town ${add.id.value}" }

            STORE.dispatch(AddStreetTile(add.id, add.tileIndex, StreetId(add.streetId)))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val town = state.getTownStorage().getOrThrow(add.id)
                showStreetEditor(call, state, town, add.streetId)
            }
        }
    }
}

private fun HTML.showStreetEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
    streetId: Int,
) {
    val backLink = href(call, town.id)
    val previewLink = call.application.href(TownRoutes.StreetRoutes.Edit(town.id))
    val createLink = call.application.href(StreetRoutes.New())

    simpleHtml("Edit Streets of Town ${town.name}") {
        split({
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectValue("Street", STREET, state.getStreetStorage().getAll(), true) { street ->
                    label = street.toString()
                    value = street.toString()
                    selected = street.id.value == streetId
                }
            }
            action(createLink, "Create new Street")
            back(backLink)
        }, {
            svg(visualizeTown(town) { index, _ ->
                call.application.href(TownRoutes.StreetRoutes.Add(town.id, streetId, index))
            }, 90)
        })
    }
}

