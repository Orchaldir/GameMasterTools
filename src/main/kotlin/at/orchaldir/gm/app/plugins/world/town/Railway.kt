package at.orchaldir.gm.app.plugins.world.town

import at.orchaldir.gm.app.CONNECTION
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.RAILWAY
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.world.parseRailwayTypeId
import at.orchaldir.gm.app.plugins.world.RailwayTypeRoutes
import at.orchaldir.gm.core.action.AddRailwayTile
import at.orchaldir.gm.core.action.RemoveRailwayTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.railway.RailwayTypeId
import at.orchaldir.gm.core.model.world.town.BuildingTile
import at.orchaldir.gm.core.model.world.town.TileConnection
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.selector.world.getBuildings
import at.orchaldir.gm.visualization.town.TownRendererConfig
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

fun Application.configureRailwayEditorRouting() {
    routing {
        get<TownRoutes.RailwayRoutes.Edit> { edit ->
            logger.info { "Get the railway editor for town ${edit.town.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(edit.town)

            call.respondHtml(HttpStatusCode.OK) {
                showRailwayEditor(call, state, town, RailwayTypeId(0), TileConnection.Horizontal)
            }
        }
        post<TownRoutes.RailwayRoutes.Preview> { preview ->
            logger.info { "Preview the railway editor for town ${preview.town.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(preview.town)
            val params = call.receiveParameters()
            val railway = parseRailwayTypeId(params, RAILWAY)
            val connection = parse(params, CONNECTION, TileConnection.Horizontal)

            call.respondHtml(HttpStatusCode.OK) {
                showRailwayEditor(call, state, town, railway, connection)
            }
        }
        get<TownRoutes.RailwayRoutes.Add> { add ->
            logger.info { "Set tile ${add.tileIndex} to railway ${add.railway.value} for town ${add.town.value}" }

            STORE.dispatch(AddRailwayTile(add.town, add.tileIndex, add.railway, add.connection))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val town = state.getTownStorage().getOrThrow(add.town)
                showRailwayEditor(call, state, town, add.railway, add.connection)
            }
        }
        get<TownRoutes.RailwayRoutes.Remove> { remove ->
            logger.info { "Remove railway from tile ${remove.tileIndex} for town ${remove.town.value}" }

            STORE.dispatch(RemoveRailwayTile(remove.town, remove.tileIndex, remove.remove))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val town = state.getTownStorage().getOrThrow(remove.town)
                showRailwayEditor(call, state, town, remove.railway, remove.connection)
            }
        }
    }
}

private fun HTML.showRailwayEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
    railway: RailwayTypeId,
    connection: TileConnection,
) {
    val backLink = href(call, town.id)
    val previewLink = call.application.href(TownRoutes.RailwayRoutes.Preview(town.id))
    val createLink = call.application.href(RailwayTypeRoutes.New())

    simpleHtml("Edit Railways of Town ${town.name}") {
        split({
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectValue("Railway", RAILWAY, state.getRailwayTypeStorage().getAll(), true) { r ->
                    label = r.name
                    value = r.id.value.toString()
                    selected = r.id == railway
                }
                selectValue("Connection", CONNECTION, TileConnection.entries, true) { c ->
                    label = c.name
                    value = c.name
                    selected = c == connection
                }
            }
            action(createLink, "Create new Railway")
            back(backLink)
        }, {
            svg(visualizeRailwayEditor(call, state, town, railway, connection), 90)
        })
    }
}

fun visualizeRailwayEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
    railway: RailwayTypeId,
    connection: TileConnection,
) = visualizeTown(
    town,
    state.getBuildings(town.id),
    createConfigWithLinks(call, state).copy(
        tileLinkLookup = { index, tile ->
            if (tile.canBuildRailway()) {
                call.application.href(TownRoutes.RailwayRoutes.Add(town.id, index, railway, connection))
            } else {
                null
            }
        },
        railwayLinkLookup = { index, remove ->
            call.application.href(
                TownRoutes.RailwayRoutes.Remove(
                    town.id,
                    index,
                    remove,
                    railway,
                    connection
                )
            )
        }
    )
)

