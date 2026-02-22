package at.orchaldir.gm.app.routes.world.settlement

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.world.parseTerrainType
import at.orchaldir.gm.app.routes.world.RegionRoutes
import at.orchaldir.gm.app.routes.world.RiverRoutes
import at.orchaldir.gm.core.action.ResizeTerrain
import at.orchaldir.gm.core.action.SetTerrainTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.world.terrain.RegionDataType
import at.orchaldir.gm.core.model.world.settlement.TerrainType
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.core.selector.world.*
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.map.Resize
import at.orchaldir.gm.visualization.settlement.showTerrainName
import at.orchaldir.gm.visualization.settlement.visualizeSettlementMap
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlinx.html.p
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureTerrainRouting() {
    routing {
        get<SettlementMapRoutes.TerrainRoutes.Edit> { edit ->
            logger.info { "Get the terrain editor for town map ${edit.id.value}" }

            val state = STORE.getState()
            val town = state.getSettlementMapStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, town, TerrainType.Plain, 0)
            }
        }
        post<SettlementMapRoutes.TerrainRoutes.Preview> { preview ->
            logger.info { "Preview the terrain editor for town map ${preview.id.value}" }

            val state = STORE.getState()
            val townMap = state.getSettlementMapStorage().getOrThrow(preview.id)
            val params = call.receiveParameters()
            val terrainType = parseTerrainType(params)
            val terrainId: Int = parseInt(params, TERRAIN, 0)

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, townMap, terrainType, terrainId)
            }
        }
        get<SettlementMapRoutes.TerrainRoutes.Update> { update ->
            logger.info { "Update the terrain to ${update.terrainType} with id ${update.terrainId} for tile ${update.tileIndex} for town map ${update.id.value}" }

            STORE.dispatch(SetTerrainTile(update.id, update.terrainType, update.terrainId, update.tileIndex))

            val state = STORE.getState()
            state.save()
            val townMap = state.getSettlementMapStorage().getOrThrow(update.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, townMap, update.terrainType, update.terrainId)
            }
        }
        post<SettlementMapRoutes.TerrainRoutes.Resize> { update ->
            logger.info { "Resize the terrain of town map ${update.id.value}" }

            val params = call.receiveParameters()
            val terrainType = parseTerrainType(params)
            val terrainId: Int = parseInt(params, TERRAIN, 0)
            val resize = Resize(
                parseInt(params, combine(WIDTH, START), 0),
                parseInt(params, combine(WIDTH, END), 0),
                parseInt(params, combine(HEIGHT, START), 0),
                parseInt(params, combine(HEIGHT, END), 0),
            )


            STORE.dispatch(ResizeTerrain(update.id, resize, terrainType, terrainId))

            STORE.getState().save()

            call.respondRedirect(call.application.href(SettlementMapRoutes.TerrainRoutes.Edit(update.id)))
        }
    }
}

private fun HTML.showTerrainEditor(
    call: ApplicationCall,
    state: State,
    settlementMap: SettlementMap,
    terrainType: TerrainType,
    terrainId: Int,
) {
    val backLink = href(call, settlementMap.id)
    val previewLink = call.application.href(SettlementMapRoutes.TerrainRoutes.Preview(settlementMap.id))
    val resizeLink = call.application.href(SettlementMapRoutes.TerrainRoutes.Resize(settlementMap.id))

    simpleHtml("Edit Terrain of Town Map ${settlementMap.name(state)}") {
        split({
            formWithPreview(previewLink, resizeLink, backLink, "Resize") {
                editTerrain(call, state, terrainType, terrainId, settlementMap)
            }
        }, {
            svg(
                visualizeSettlementMap(
                    settlementMap, state.getBuildingsIn(settlementMap.id),
                    tileLinkLookup = { index, _ ->
                        call.application.href(
                            SettlementMapRoutes.TerrainRoutes.Update(
                                settlementMap.id,
                                terrainType,
                                terrainId,
                                index
                            )
                        )
                    },
                    tileTooltipLookup = showTerrainName(state),
                ), 90
            )
        })
    }
}

private fun HtmlBlockTag.editTerrain(
    call: ApplicationCall,
    state: State,
    terrainType: TerrainType,
    terrainId: Int,
    settlementMap: SettlementMap,
) {
    val createMountainLink = call.application.href(RegionRoutes.New())
    val createRiverLink = call.application.href(RiverRoutes.New())
    val rivers = state.getRiverStorage().getAll()
    val mountains = state.getRegions(RegionDataType.Mountain)

    selectValue("Terrain", combine(TERRAIN, TYPE), TerrainType.entries, terrainType) { type ->
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

    field("Size", settlementMap.map.size.format())
    val maxDelta = 100
    selectInt(
        "Add/Remove Columns At Start",
        0,
        getMinWidthStart(settlementMap),
        maxDelta,
        1,
        combine(WIDTH, START)
    )
    selectInt("Add/Remove Columns At End", 0, getMinWidthEnd(settlementMap), maxDelta, 1, combine(WIDTH, END))
    selectInt("Add/Remove Rows At Start", 0, getMinHeightStart(settlementMap), maxDelta, 1, combine(HEIGHT, START))
    selectInt("Add/Remove Rows At End", 0, getMinHeightEnd(settlementMap), maxDelta, 1, combine(HEIGHT, END))
}

private fun <ID : Id<ID>> HtmlBlockTag.selectTerrain(
    text: String,
    options: Collection<ElementWithSimpleName<ID>>,
    id: Int,
) {
    selectValue(text, TERRAIN, options) { m ->
        label = m.name()
        value = m.id().value().toString()
        selected = id == m.id().value()
    }
}
