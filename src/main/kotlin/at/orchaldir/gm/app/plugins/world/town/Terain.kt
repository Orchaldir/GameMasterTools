package at.orchaldir.gm.app.plugins.world.town

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.world.parseTerrainType
import at.orchaldir.gm.app.plugins.world.MountainRoutes
import at.orchaldir.gm.app.plugins.world.RiverRoutes
import at.orchaldir.gm.core.action.ResizeTown
import at.orchaldir.gm.core.action.SetTerrainTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.TerrainType
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.visualization.town.visualizeTerrain
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureTerrainRouting() {
    routing {
        get<TownRoutes.TerrainRoutes.Edit> { edit ->
            logger.info { "Get the terrain editor for town ${edit.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, town, TerrainType.Plain, 0)
            }
        }
        post<TownRoutes.TerrainRoutes.Preview> { preview ->
            logger.info { "Preview the terrain editor for town ${preview.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(preview.id)
            val params = call.receiveParameters()
            val terrainType = parseTerrainType(params)
            val terrainId: Int = parseInt(params, TERRAIN, 0)

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, town, terrainType, terrainId)
            }
        }
        get<TownRoutes.TerrainRoutes.Update> { update ->
            logger.info { "Update the terrain to ${update.terrainType} with id ${update.terrainId} for tile ${update.tileIndex} for town ${update.id.value}" }

            STORE.dispatch(SetTerrainTile(update.id, update.terrainType, update.terrainId, update.tileIndex))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val town = state.getTownStorage().getOrThrow(update.id)
                showTerrainEditor(call, state, town, update.terrainType, update.terrainId)
            }
        }
        get<TownRoutes.TerrainRoutes.Resize> { update ->
            logger.info { "Resize the terrain of town ${update.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(update.id)
            val params = call.receiveParameters()
            val terrainType = parseTerrainType(params)
            val terrainId: Int = parseInt(params, TERRAIN, 0)
            val widthStart = parseInt(params, combine(WIDTH, START), 0)
            val widthEnd = parseInt(params, combine(WIDTH, END), 0)
            val heightStart = parseInt(params, combine(HEIGHT, START), 0)
            val heightEnd = parseInt(params, combine(HEIGHT, END), 0)

            STORE.dispatch(ResizeTown(update.id, terrainType, terrainId, widthStart, widthEnd, heightStart, heightEnd))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, town, terrainType, terrainId)
            }
        }
    }
}

private fun HTML.showTerrainEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
    terrainType: TerrainType,
    terrainId: Int,
) {
    val backLink = href(call, town.id)
    val previewLink = call.application.href(TownRoutes.TerrainRoutes.Preview(town.id))
    val createMountainLink = call.application.href(MountainRoutes.New())
    val createRiverLink = call.application.href(RiverRoutes.New())

    simpleHtml("Edit Terrain of Town ${town.name}") {
        split({
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectValue("Terrain", combine(TERRAIN, TYPE), TerrainType.entries, true) { type ->
                    label = type.toString()
                    value = type.toString()
                    selected = type == terrainType
                }
                when (terrainType) {
                    TerrainType.Hill, TerrainType.Mountain -> selectTerrain(
                        "Mountain",
                        state.getMountainStorage().getAll(),
                        terrainId,
                        createMountainLink,
                    )

                    TerrainType.Plain -> doNothing()
                    TerrainType.River -> selectTerrain(
                        "River",
                        state.getRiverStorage().getAll(),
                        terrainId,
                        createRiverLink,
                    )
                }
                h2 { +"Resize" }
                val maxDelta = 100
                selectInt("Add/Remove Columns At Start", 0, -maxDelta, maxDelta, combine(WIDTH, START))
                selectInt("Add/Remove Columns At End", 0, -maxDelta, maxDelta, combine(WIDTH, END))
                selectInt("Add/Remove Rows At Start", 0, -maxDelta, maxDelta, combine(HEIGHT, START))
                selectInt("Add/Remove Rows At End", 0, -maxDelta, maxDelta, combine(HEIGHT, END))
            }
            back(backLink)
        }, {
            svg(visualizeTerrain(town) { index, _ ->
                call.application.href(TownRoutes.TerrainRoutes.Update(town.id, terrainType, terrainId, index))
            }, 90)
        })
    }
}

private fun <ID : Id<ID>> FORM.selectTerrain(text: String, options: Collection<Element<ID>>, id: Int, link: String) {
    selectValue(text, TERRAIN, options, true) { m ->
        label = m.name()
        value = m.id().value().toString()
        selected = id == m.id().value()
    }
    action(link, "Create new $text")
}
