package at.orchaldir.gm.app.plugins.world.town

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.TERRAIN
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.update
import at.orchaldir.gm.visualization.town.visualizeTown
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
            logger.info { "a" }
            val town = state.getTownStorage().getOrThrow(preview.id)
            logger.info { "b" }
            val params = call.receiveParameters()
            logger.info { "c" }
            val terrainType = parse(params, combine(TERRAIN, TYPE), TerrainType.Plain)
            logger.info { "d" }
            val terrainId: Int = parseInt(params, TERRAIN, 0)

            logger.info { "f" }

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, town, terrainType, terrainId)
            }
        }
        get<TownRoutes.TerrainRoutes.Update> { update ->
            logger.info { "Update the terrain for town ${update.id.value}" }

            val state = STORE.getState()
            val oldTown = state.getTownStorage().getOrThrow(update.id)
            val terrain = when (update.terrainType) {
                TerrainType.Hill -> HillTerrain(MountainId(update.terrainId))
                TerrainType.Mountain -> MountainTerrain(MountainId(update.terrainId))
                TerrainType.Plain -> PlainTerrain
                TerrainType.River -> RiverTerrain(RiverId(update.terrainId))
            }
            val tile = oldTown.map.tiles[update.tileIndex].copy(terrain = terrain)
            val tiles = oldTown.map.tiles.update(update.tileIndex, tile)
            val town = oldTown.copy(map = oldTown.map.copy(tiles = tiles))

            STORE.dispatch(UpdateTown(town))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, town, update.terrainType, update.terrainId)
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
                        terrainId
                    )

                    TerrainType.Plain -> doNothing()
                    TerrainType.River -> selectTerrain("River", state.getMountainStorage().getAll(), terrainId)
                }
            }
            back(backLink)
        }, {
            svg(visualizeTown(town) { index, _ ->
                call.application.href(TownRoutes.TerrainRoutes.Update(town.id, terrainType, terrainId, index))
            }, 90)
        })
    }
}

private fun <ID : Id<ID>> FORM.selectTerrain(text: String, options: Collection<Element<ID>>, id: Int) {
    selectValue(text, TERRAIN, options, true) { m ->
        label = m.name()
        value = m.id().value().toString()
        selected = id == m.id().value()
    }
}
