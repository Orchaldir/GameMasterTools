package at.orchaldir.gm.app.routes.world.town

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.back
import at.orchaldir.gm.app.html.href
import at.orchaldir.gm.app.html.simpleHtml
import at.orchaldir.gm.app.html.svg
import at.orchaldir.gm.app.routes.world.BuildingRoutes
import at.orchaldir.gm.app.routes.world.town.TownRoutes.AbstractBuildingRoutes.Add
import at.orchaldir.gm.app.routes.world.town.TownRoutes.AbstractBuildingRoutes.Remove
import at.orchaldir.gm.core.action.AddAbstractBuilding
import at.orchaldir.gm.core.action.RemoveAbstractBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.town.AbstractBuildingTile
import at.orchaldir.gm.core.model.world.town.NoConstruction
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.core.selector.world.getBuildings
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.visualization.town.visualizeTown
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.html.HTML
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureAbstractBuildingEditorRouting() {
    routing {
        get<TownRoutes.AbstractBuildingRoutes.Edit> { edit ->
            logger.info { "Get the abstract building editor for town ${edit.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showAbstractBuildingEditor(call, state, town)
            }
        }
        get<Add> { add ->
            logger.info { "Add a new abstract building" }

            STORE.dispatch(AddAbstractBuilding(add.town, add.tileIndex))
            STORE.getState().save()

            redirectToEdit(add.town)
        }
        get<Remove> { remove ->
            logger.info { "Remove an abstract building" }

            STORE.dispatch(RemoveAbstractBuilding(remove.town, remove.tileIndex))
            STORE.getState().save()

            redirectToEdit(remove.town)
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.redirectToEdit(
    townId: TownId,
) {
    call.respondRedirect(call.application.href(TownRoutes.AbstractBuildingRoutes.Edit(townId)))
}

private fun HTML.showAbstractBuildingEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
) {
    val backLink = href(call, town.id)

    simpleHtml("Edit Abstract Buildings of Town ${town.name()}") {
        svg(visualizeAbstractBuildingEditor(call, state, town), 90)
        back(backLink)
    }
}

fun visualizeAbstractBuildingEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
): Svg {
    return visualizeTown(
        town, state.getBuildings(town.id),
        tileLinkLookup = { index, tile ->
            when (tile.construction) {
                NoConstruction -> call.application.href(Add(town.id, index))
                AbstractBuildingTile -> call.application.href(Remove(town.id, index))
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

