package at.orchaldir.gm.app.routes.world.settlement

import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.routes.world.BuildingRoutes
import at.orchaldir.gm.core.action.AddBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.visualization.settlement.visualizeSettlementMap
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

fun Application.configureBuildingEditorRouting() {
    routing {
        get<SettlementMapRoutes.BuildingRoutes.Edit> { edit ->
            logger.info { "Get the building editor for town map ${edit.id.value}" }

            val state = STORE.getState()
            val townMap = state.getSettlementMapStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBuildingEditor(call, state, townMap, MapSize2d.square(1))
            }
        }
        post<SettlementMapRoutes.BuildingRoutes.Preview> { preview ->
            logger.info { "Preview the building editor for town map ${preview.id.value}" }

            val state = STORE.getState()
            val townMap = state.getSettlementMapStorage().getOrThrow(preview.id)
            val params = call.receiveParameters()
            val size = MapSize2d(
                parseInt(params, WIDTH, 1),
                parseInt(params, HEIGHT, 1),
            )

            call.respondHtml(HttpStatusCode.OK) {
                showBuildingEditor(call, state, townMap, size)
            }
        }
        get<SettlementMapRoutes.BuildingRoutes.Add> { add ->
            logger.info { "Add a new building to town map ${add.settlement.value}" }

            STORE.dispatch(AddBuilding(add.settlement, add.tileIndex, add.size))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val townMap = state.getSettlementMapStorage().getOrThrow(add.settlement)
                showBuildingEditor(call, state, townMap, add.size)
            }
        }
    }
}

private fun HTML.showBuildingEditor(
    call: ApplicationCall,
    state: State,
    settlementMap: SettlementMap,
    size: MapSize2d,
) {
    val backLink = href(call, settlementMap.id)
    val previewLink = call.application.href(SettlementMapRoutes.BuildingRoutes.Preview(settlementMap.id))

    simpleHtml("Edit Buildings of Town Map ${settlementMap.name(state)}") {
        split({
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectInt("Width", size.width, 1, 10, 1, WIDTH)
                selectInt("Height", size.height, 1, 10, 1, HEIGHT)
            }
            back(backLink)
        }, {
            svg(visualizeBuildingEditor(call, state, settlementMap, size), 90)
        })
    }
}

fun visualizeBuildingEditor(
    call: ApplicationCall,
    state: State,
    settlementMap: SettlementMap,
    size: MapSize2d,
): Svg {
    return visualizeSettlementMap(
        settlementMap, state.getBuildingsIn(settlementMap.id),
        tileLinkLookup = { index, _ ->
            if (settlementMap.canBuild(index, size)) {
                call.application.href(SettlementMapRoutes.BuildingRoutes.Add(settlementMap.id, index, size))
            } else {
                null
            }
        },
        buildingLinkLookup = { building ->
            call.application.href(BuildingRoutes.Details(building.id))
        },
        buildingTooltipLookup = { building ->
            building.name(state)
        },
    )
}

