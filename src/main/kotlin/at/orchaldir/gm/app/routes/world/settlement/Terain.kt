package at.orchaldir.gm.app.routes.world.settlement

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.TERRAIN
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.world.editTerrain
import at.orchaldir.gm.app.html.world.parseTerrainResize
import at.orchaldir.gm.app.html.world.parseTerrainType
import at.orchaldir.gm.core.action.ResizeTerrain
import at.orchaldir.gm.core.action.SetTerrainTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.model.world.settlement.TerrainType
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
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureTerrainRouting() {
    routing {
        get<SettlementMapRoutes.TerrainRoutes.Edit> { edit ->
            logger.info { "Get the terrain editor for settlement map ${edit.id.value}" }

            val state = STORE.getState()
            val settlement = state.getSettlementMapStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, settlement)
            }
        }
        post<SettlementMapRoutes.TerrainRoutes.Preview> { preview ->
            logger.info { "Preview the terrain editor for settlement map ${preview.id.value}" }

            val state = STORE.getState()
            val settlementMap = state.getSettlementMapStorage().getOrThrow(preview.id)
            val params = call.receiveParameters()
            val terrainType = parseTerrainType(params)
            val terrainId: Int = parseInt(params, TERRAIN, 0)
            val resize = parseTerrainResize(params)

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, settlementMap, terrainType, terrainId, resize)
            }
        }
        get<SettlementMapRoutes.TerrainRoutes.Update> { update ->
            logger.info { "Update the terrain to ${update.terrainType} with id ${update.terrainId} for tile ${update.tileIndex} for settlement map ${update.id.value}" }

            STORE.dispatch(SetTerrainTile(update.id, update.terrainType, update.terrainId, update.tileIndex))

            val state = STORE.getState()
            state.save()
            val settlementMap = state.getSettlementMapStorage().getOrThrow(update.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, settlementMap, update.terrainType, update.terrainId)
            }
        }
        post<SettlementMapRoutes.TerrainRoutes.Resize> { update ->
            logger.info { "Resize the terrain of settlement map ${update.id.value}" }

            val params = call.receiveParameters()
            val terrainType = parseTerrainType(params)
            val terrainId: Int = parseInt(params, TERRAIN, 0)
            val resize = parseTerrainResize(params)

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
    terrainType: TerrainType = TerrainType.Plain,
    terrainId: Int = 0,
    resize: Resize = Resize(),
) {
    val backLink = href(call, settlementMap.id)
    val previewLink = call.application.href(SettlementMapRoutes.TerrainRoutes.Preview(settlementMap.id))
    val resizeLink = call.application.href(SettlementMapRoutes.TerrainRoutes.Resize(settlementMap.id))

    simpleHtml("Edit Terrain of Settlement Map ${settlementMap.name(state)}") {
        split({
            formWithPreview(previewLink, resizeLink, backLink, "Resize") {
                editTerrain(call, state, terrainType, terrainId, resize, settlementMap)
            }
        }, {
            svg(
                visualizeSettlementMap(
                    state,
                    settlementMap,
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
