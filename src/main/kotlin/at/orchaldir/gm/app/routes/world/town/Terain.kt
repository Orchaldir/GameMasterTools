package at.orchaldir.gm.app.routes.world.town

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.world.parseTerrainType
import at.orchaldir.gm.app.routes.world.MountainRoutes
import at.orchaldir.gm.app.routes.world.RiverRoutes
import at.orchaldir.gm.core.action.ResizeTown
import at.orchaldir.gm.core.action.SetTerrainTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.world.terrain.TerrainType
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.selector.world.*
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.map.Resize
import at.orchaldir.gm.visualization.town.showTerrainName
import at.orchaldir.gm.visualization.town.visualizeTown
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.FORM
import kotlinx.html.HTML
import kotlinx.html.h2
import kotlinx.html.p
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

            val state = STORE.getState()
            state.save()
            val town = state.getTownStorage().getOrThrow(update.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, town, update.terrainType, update.terrainId)
            }
        }
        post<TownRoutes.TerrainRoutes.Resize> { update ->
            logger.info { "Resize the terrain of town ${update.id.value}" }

            val params = call.receiveParameters()
            val terrainType = parseTerrainType(params)
            val terrainId: Int = parseInt(params, TERRAIN, 0)
            val resize = Resize(
                parseInt(params, combine(WIDTH, START), 0),
                parseInt(params, combine(WIDTH, END), 0),
                parseInt(params, combine(HEIGHT, START), 0),
                parseInt(params, combine(HEIGHT, END), 0),
            )


            STORE.dispatch(ResizeTown(update.id, resize, terrainType, terrainId))

            STORE.getState().save()

            call.respondRedirect(call.application.href(TownRoutes.TerrainRoutes.Edit(update.id)))
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
    val resizeLink = call.application.href(TownRoutes.TerrainRoutes.Resize(town.id))

    simpleHtml("Edit Terrain of Town ${town.name()}") {
        split({
            formWithPreview(previewLink, resizeLink, backLink, "Resize") {
                editTerrain(call, state, terrainType, terrainId, town)
            }
        }, {
            svg(
                visualizeTown(
                    town, state.getBuildings(town.id),
                    tileLinkLookup = { index, _ ->
                        call.application.href(TownRoutes.TerrainRoutes.Update(town.id, terrainType, terrainId, index))
                    },
                    tileTooltipLookup = showTerrainName(state),
                ), 90
            )
        })
    }
}

private fun FORM.editTerrain(
    call: ApplicationCall,
    state: State,
    terrainType: TerrainType,
    terrainId: Int,
    town: Town,
) {
    val createMountainLink = call.application.href(MountainRoutes.New())
    val createRiverLink = call.application.href(RiverRoutes.New())
    val rivers = state.getRiverStorage().getAll()
    val mountains = state.getMountainStorage().getAll()

    selectValue("Terrain", combine(TERRAIN, TYPE), TerrainType.entries, terrainType, true) { type ->
        when (type) {
            TerrainType.Hill, TerrainType.Mountain -> mountains.isEmpty()
            TerrainType.Plain -> false
            TerrainType.River -> rivers.isEmpty()
        }
    }
    when (terrainType) {
        TerrainType.Hill, TerrainType.Mountain -> selectTerrain(
            "Mountain",
            mountains,
            terrainId,
        )

        TerrainType.Plain -> doNothing()
        TerrainType.River -> selectTerrain(
            "River",
            rivers,
            terrainId,
        )
    }
    action(createMountainLink, "Create new Mountain")
    action(createRiverLink, "Create new River")

    h2 { +"Update Terrain of Tile" }

    p { +"Click on a tile to change it's terrain to the type above." }

    h2 { +"Resize" }

    field("Size", town.map.size.format())
    val maxDelta = 100
    selectInt(
        "Add/Remove Columns At Start",
        0,
        getMinWidthStart(town),
        maxDelta,
        1,
        combine(WIDTH, START)
    )
    selectInt("Add/Remove Columns At End", 0, getMinWidthEnd(town), maxDelta, 1, combine(WIDTH, END))
    selectInt("Add/Remove Rows At Start", 0, getMinHeightStart(town), maxDelta, 1, combine(HEIGHT, START))
    selectInt("Add/Remove Rows At End", 0, getMinHeightEnd(town), maxDelta, 1, combine(HEIGHT, END))
}

private fun <ID : Id<ID>> FORM.selectTerrain(
    text: String,
    options: Collection<ElementWithSimpleName<ID>>,
    id: Int,
) {
    selectValue(text, TERRAIN, options, true) { m ->
        label = m.name()
        value = m.id().value().toString()
        selected = id == m.id().value()
    }
}
