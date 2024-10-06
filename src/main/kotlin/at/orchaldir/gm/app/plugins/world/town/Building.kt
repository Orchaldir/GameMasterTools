package at.orchaldir.gm.app.plugins.world.town

import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.plugins.world.BuildingRoutes
import at.orchaldir.gm.core.action.AddBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.selector.world.getBuildings
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.visualization.town.SHOW_BUILDING_NAME
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

fun Application.configureBuildingEditorRouting() {
    routing {
        get<TownRoutes.BuildingRoutes.Edit> { edit ->
            logger.info { "Get the building editor for town ${edit.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBuildingEditor(call, state, town, MapSize2d.square(1))
            }
        }
        post<TownRoutes.BuildingRoutes.Preview> { preview ->
            logger.info { "Preview the building editor for town ${preview.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(preview.id)
            val params = call.receiveParameters()
            val size = MapSize2d(parseInt(params, WIDTH, 1), parseInt(params, HEIGHT, 1))

            call.respondHtml(HttpStatusCode.OK) {
                showBuildingEditor(call, state, town, size)
            }
        }
        get<TownRoutes.BuildingRoutes.Add> { add ->
            logger.info { "Add new building" }

            STORE.dispatch(AddBuilding(add.town, add.tileIndex, add.size))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val town = state.getTownStorage().getOrThrow(add.town)
                showBuildingEditor(call, state, town, add.size)
            }
        }
    }
}

private fun HTML.showBuildingEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
    size: MapSize2d,
) {
    val backLink = href(call, town.id)
    val previewLink = call.application.href(TownRoutes.BuildingRoutes.Preview(town.id))

    simpleHtml("Edit Buildings of Town ${town.name}") {
        split({
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectInt("Width", size.width, 1, 10, WIDTH, true)
                selectInt("Height", size.height, 1, 10, HEIGHT, true)
            }
            back(backLink)
        }, {
            svg(visualizeBuildingEditor(call, state, town, size), 90)
        })
    }
}

fun visualizeBuildingEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
    size: MapSize2d,
): Svg {
    return visualizeTown(
        town,
        state.getBuildings(town.id),
        TownRendererConfig(state).copy(
            tileLinkLookup = { index, _ ->
                if (town.canBuild(index, size)) {
                    call.application.href(TownRoutes.BuildingRoutes.Add(town.id, index, size))
                } else {
                    null
                }
            },
            buildingLinkLookup = { building ->
                call.application.href(BuildingRoutes.Details(building.id))
            },
        )
    )
}

