package at.orchaldir.gm.app.routes.world.settlement

import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.routes.world.BuildingRoutes
import at.orchaldir.gm.app.routes.world.settlement.SettlementMapRoutes.AbstractBuildingRoutes.*
import at.orchaldir.gm.core.action.AddAbstractBuilding
import at.orchaldir.gm.core.action.RemoveAbstractBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.settlement.*
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
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.html.FormMethod
import kotlinx.html.HTML
import kotlinx.html.form
import kotlinx.html.id
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureAbstractBuildingEditorRouting() {
    routing {
        get<Edit> { edit ->
            logger.info { "Get the abstract building editor for settlement map ${edit.id.value}" }

            val state = STORE.getState()
            val townMap = state.getSettlementMapStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showAbstractBuildingEditor(call, state, townMap, edit.size)
            }
        }
        post<Preview> { preview ->
            logger.info { "Preview the abstract building editor for settlement map ${preview.id.value}" }

            val state = STORE.getState()
            val townMap = state.getSettlementMapStorage().getOrThrow(preview.id)
            val params = call.receiveParameters()
            val size = MapSize2d(
                parseInt(params, WIDTH, 1),
                parseInt(params, HEIGHT, 1),
            )

            call.respondHtml(HttpStatusCode.OK) {
                showAbstractBuildingEditor(call, state, townMap, size)
            }
        }
        get<Add> { add ->
            logger.info { "Add a new abstract building to settlement map ${add.settlement.value}" }

            STORE.dispatch(AddAbstractBuilding(add.settlement, add.tileIndex, add.size))
            STORE.getState().save()

            redirectToEdit(add.settlement, add.size)
        }
        get<Remove> { remove ->
            logger.info { "Remove an abstract building from settlement map ${remove.settlement.value}" }

            STORE.dispatch(RemoveAbstractBuilding(remove.settlement, remove.tileIndex))
            STORE.getState().save()

            redirectToEdit(remove.settlement)
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.redirectToEdit(
    settlementMapId: SettlementMapId,
    size: MapSize2d = MapSize2d.square(1),
) {
    call.respondRedirect(call.application.href(Edit(settlementMapId, size)))
}

private fun HTML.showAbstractBuildingEditor(
    call: ApplicationCall,
    state: State,
    settlementMap: SettlementMap,
    size: MapSize2d = MapSize2d.square(1),
) {
    val backLink = href(call, settlementMap.id)
    val previewLink = call.application.href(Preview(settlementMap.id))

    simpleHtml("Edit Abstract Buildings of Settlement ${settlementMap.name(state)}") {
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
            svg(visualizeAbstractBuildingEditor(call, state, settlementMap, size), 90)
        })
    }
}

fun visualizeAbstractBuildingEditor(
    call: ApplicationCall,
    state: State,
    settlement: SettlementMap,
    size: MapSize2d,
): Svg {
    return visualizeSettlementMap(
        settlement, state.getBuildingsIn(settlement.id),
        tileLinkLookup = { index, tile ->
            when (tile.construction) {
                NoConstruction -> call.application.href(Add(settlement.id, index, size))
                AbstractBuildingTile, is AbstractLargeBuildingStart, AbstractLargeBuildingTile ->
                    call.application.href(Remove(settlement.id, index))

                else -> null
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

