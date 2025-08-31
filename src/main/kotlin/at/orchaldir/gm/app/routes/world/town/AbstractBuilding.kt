package at.orchaldir.gm.app.routes.world.town

import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.routes.world.BuildingRoutes
import at.orchaldir.gm.app.routes.world.town.TownMapRoutes.AbstractBuildingRoutes.*
import at.orchaldir.gm.core.action.AddAbstractBuilding
import at.orchaldir.gm.core.action.RemoveAbstractBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.town.*
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.visualization.town.visualizeTown
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
            logger.info { "Get the abstract building editor for town map ${edit.id.value}" }

            val state = STORE.getState()
            val townMap = state.getTownMapStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showAbstractBuildingEditor(call, state, townMap, edit.size)
            }
        }
        post<Preview> { preview ->
            logger.info { "Preview the abstract building editor for town map ${preview.id.value}" }

            val state = STORE.getState()
            val townMap = state.getTownMapStorage().getOrThrow(preview.id)
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
            logger.info { "Add a new abstract building to town map ${add.town.value}" }

            STORE.dispatch(AddAbstractBuilding(add.town, add.tileIndex, add.size))
            STORE.getState().save()

            redirectToEdit(add.town, add.size)
        }
        get<Remove> { remove ->
            logger.info { "Remove an abstract building from town map ${remove.town.value}" }

            STORE.dispatch(RemoveAbstractBuilding(remove.town, remove.tileIndex))
            STORE.getState().save()

            redirectToEdit(remove.town)
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.redirectToEdit(
    townMapId: TownMapId,
    size: MapSize2d = MapSize2d.square(1),
) {
    call.respondRedirect(call.application.href(Edit(townMapId, size)))
}

private fun HTML.showAbstractBuildingEditor(
    call: ApplicationCall,
    state: State,
    townMap: TownMap,
    size: MapSize2d = MapSize2d.square(1),
) {
    val backLink = href(call, townMap.id)
    val previewLink = call.application.href(Preview(townMap.id))

    simpleHtml("Edit Abstract Buildings of Town ${townMap.name(state)}") {
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
            svg(visualizeAbstractBuildingEditor(call, state, townMap, size), 90)
        })
    }
}

fun visualizeAbstractBuildingEditor(
    call: ApplicationCall,
    state: State,
    town: TownMap,
    size: MapSize2d,
): Svg {
    return visualizeTown(
        town, state.getBuildingsIn(town.id),
        tileLinkLookup = { index, tile ->
            when (tile.construction) {
                NoConstruction -> call.application.href(Add(town.id, index, size))
                AbstractBuildingTile, is AbstractLargeBuildingStart, AbstractLargeBuildingTile ->
                    call.application.href(Remove(town.id, index))

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

