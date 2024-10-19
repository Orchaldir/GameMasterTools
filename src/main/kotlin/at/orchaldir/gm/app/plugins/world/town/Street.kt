package at.orchaldir.gm.app.plugins.world.town

import at.orchaldir.gm.app.CONNECTION
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.STREET
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.plugins.world.StreetRoutes
import at.orchaldir.gm.core.action.AddStreetTile
import at.orchaldir.gm.core.action.RemoveStreetTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.TileConnection
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.selector.world.getBuildings
import at.orchaldir.gm.visualization.town.TownRendererConfig
import at.orchaldir.gm.visualization.town.showSelectedElement
import at.orchaldir.gm.visualization.town.visualizeTown
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
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

            call.respondHtml(HttpStatusCode.OK) {
                showStreetEditor(call, state, town, StreetId(0), TileConnection.Horizontal)
            }
        }
        post<TownRoutes.StreetRoutes.Preview> { preview ->
            logger.info { "Preview the street editor for town ${preview.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(preview.id)
            val params = call.receiveParameters()
            val streetId: Int = parseInt(params, STREET, 0)
            val connection = parse(params, CONNECTION, TileConnection.Horizontal)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetEditor(call, state, town, StreetId(streetId), connection)
            }
        }
        get<TownRoutes.StreetRoutes.Add> { add ->
            logger.info { "Set tile ${add.tileIndex} to street ${add.streetId.value} for town ${add.id.value}" }

            STORE.dispatch(AddStreetTile(add.id, add.tileIndex, add.streetId, add.connection))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val town = state.getTownStorage().getOrThrow(add.id)
                showStreetEditor(call, state, town, add.streetId, add.connection)
            }
        }
        get<TownRoutes.StreetRoutes.Remove> { remove ->
            logger.info { "Remove street from tile ${remove.tileIndex} for town ${remove.id.value}" }

            STORE.dispatch(RemoveStreetTile(remove.id, remove.tileIndex, remove.remove))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val town = state.getTownStorage().getOrThrow(remove.id)
                showStreetEditor(call, state, town, remove.street, remove.connection)
            }
        }
    }
}

private fun HTML.showStreetEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
    streetId: StreetId,
    connection: TileConnection,
) {
    val backLink = href(call, town.id)
    val previewLink = call.application.href(TownRoutes.StreetRoutes.Preview(town.id))
    val createLink = call.application.href(StreetRoutes.New())

    simpleHtml("Edit Streets of Town ${town.name}") {
        split({
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectValue("Street", STREET, state.getStreetStorage().getAll(), true) { street ->
                    label = street.name
                    value = street.id.value.toString()
                    selected = street.id == streetId
                }
                selectValue("Connection", CONNECTION, TileConnection.entries, true) { c ->
                    label = c.name
                    value = c.name
                    selected = c == connection
                }
            }
            action(createLink, "Create new Street")
            back(backLink)
        }, {
            svg(visualizeStreetEditor(call, state, town, streetId, connection), 90)
        })
    }
}

fun visualizeStreetEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
    street: StreetId,
    connection: TileConnection,
) = visualizeTown(
    town, state.getBuildings(town.id),
    TownRendererConfig(state).copy(
        tileLinkLookup = { index, _ ->
            if (town.canBuildStreet(index, street, connection)) {
                call.application.href(TownRoutes.StreetRoutes.Add(town.id, index, street, connection))
            } else {
                null
            }
        },
        streetColorLookup = showSelectedElement(street),
        streetLinkLookup = { index, remove ->
            call.application.href(TownRoutes.StreetRoutes.Remove(town.id, index, remove, street, connection))
        },
    )
)

