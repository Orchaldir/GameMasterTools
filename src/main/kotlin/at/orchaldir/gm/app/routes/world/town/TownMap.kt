package at.orchaldir.gm.app.routes.world.town

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.showLocalElements
import at.orchaldir.gm.app.html.world.editTownMap
import at.orchaldir.gm.app.html.world.parseTownMap
import at.orchaldir.gm.app.html.world.showCharactersOfTownMap
import at.orchaldir.gm.app.html.world.showTownMap
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.world.BuildingRoutes
import at.orchaldir.gm.app.routes.world.StreetRoutes
import at.orchaldir.gm.app.routes.world.town.TownMapRoutes.AbstractBuildingRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.selector.character.countResidents
import at.orchaldir.gm.core.selector.util.countBuildingsIn
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.core.selector.util.sortTownMaps
import at.orchaldir.gm.core.selector.world.getRegions
import at.orchaldir.gm.core.selector.world.getRivers
import at.orchaldir.gm.core.selector.world.getStreets
import at.orchaldir.gm.visualization.town.getStreetTemplateFill
import at.orchaldir.gm.visualization.town.showTerrainName
import at.orchaldir.gm.visualization.town.visualizeTown
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlin.let

fun Application.configureTownMapRouting() {
    routing {
        get<TownMapRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                TownMapRoutes(),
                state.sortTownMaps(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Town") { tdLink(call, state, it.town) },
                    createStartDateColumn(call, state),
                    countColumnForId("Buildings", state::countBuildingsIn),
                    countColumnForId("Resident", state::countResidents),
                ),
            )
        }
        get<TownMapRoutes.Details> { details ->
            handleShowElementSplit(details.id, TownMapRoutes(), HtmlBlockTag::showTownMapDetails) { _, state, townMap ->
                svg(visualizeTownWithLinks(call, state, townMap), 90)
            }
        }
        get<TownMapRoutes.New> {
            handleCreateElement(TownMapRoutes(), STORE.getState().getTownMapStorage())
        }
        get<TownMapRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, TownMapRoutes.All())
        }
        get<TownMapRoutes.Edit> { edit ->
            handleEditElementSplit(
                edit.id,
                TownMapRoutes(),
                HtmlBlockTag::editTownMap,
                HtmlBlockTag::showTownMapEditorRight,
            )
        }
        post<TownMapRoutes.Preview> { preview ->
            handlePreviewElementSplit(
                preview.id,
                TownMapRoutes(),
                { state, parameters, id -> parseTownMap(state, parameters, state.getTownMapStorage().getOrThrow(id)) },
                HtmlBlockTag::editTownMap,
                HtmlBlockTag::showTownMapEditorRight,
            )
        }
        post<TownMapRoutes.Update> { update ->
            handleUpdateElement(update.id) { state, parameters, id ->
                val oldTownMap = state.getTownMapStorage().getOrThrow(id)

                parseTownMap(state, parameters, oldTownMap)
            }
        }
    }
}

private fun HtmlBlockTag.showTownMapDetails(
    call: ApplicationCall,
    state: State,
    townMap: TownMap,
) {
    val editAbstractBuildingsLink = call.application.href(AbstractBuildingRoutes.Edit(townMap.id))
    val editBuildingsLink = call.application.href(TownMapRoutes.BuildingRoutes.Edit(townMap.id))
    val editStreetsLink = call.application.href(TownMapRoutes.StreetRoutes.Edit(townMap.id))
    val editTerrainLink = call.application.href(TownMapRoutes.TerrainRoutes.Edit(townMap.id))

    showTownMap(call, state, townMap)

    action(editAbstractBuildingsLink, "Edit Abstract Buildings")
    action(editBuildingsLink, "Edit Buildings")

    h2 { +"Terrain" }

    fieldElements(call, state, state.getRegions(townMap.id))
    fieldElements(call, state, state.getRivers(townMap.id))
    fieldElements(call, state, state.getStreets(townMap.id))

    showStreetTemplateCount(call, state, townMap.id)

    action(editStreetsLink, "Edit Streets")
    action(editTerrainLink, "Edit Terrain")

    showLocalElements(call, state, townMap.id)
    showCharactersOfTownMap(call, state, townMap.town, townMap.id)

}

private fun HtmlBlockTag.showTownMapEditorRight(
    call: ApplicationCall,
    state: State,
    townMap: TownMap,
) {
    svg(visualizeTownWithLinks(call, state, townMap), 90)
}

private fun visualizeTownWithLinks(
    call: ApplicationCall,
    state: State,
    town: TownMap,
) = visualizeTown(
    town, state.getBuildingsIn(town.id),
    tileTooltipLookup = showTerrainName(state),
    buildingLinkLookup = { building ->
        call.application.href(BuildingRoutes.Details(building.id))
    },
    buildingTooltipLookup = { building ->
        building.name(state)
    },
    streetLinkLookup = { tile, _ ->
        tile.streetId?.let { call.application.href(StreetRoutes.Details(it)) }
    },
    streetTooltipLookup = { tile, _ ->
        state.getStreetStorage().getOptional(tile.streetId)?.name(state)
    },
    streetColorLookup = getStreetTemplateFill(state),
)
