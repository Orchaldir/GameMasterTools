package at.orchaldir.gm.app.routes.world.settlement

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.showLocalElements
import at.orchaldir.gm.app.html.world.editSettlementMap
import at.orchaldir.gm.app.html.world.parseSettlementMap
import at.orchaldir.gm.app.html.world.showCharactersOfSettlementMap
import at.orchaldir.gm.app.html.world.showSettlementMap
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.world.BuildingRoutes
import at.orchaldir.gm.app.routes.world.StreetRoutes
import at.orchaldir.gm.app.routes.world.settlement.SettlementMapRoutes.AbstractBuildingRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.selector.character.countResidents
import at.orchaldir.gm.core.selector.util.countBuildingsIn
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.core.selector.util.sortSettlementMaps
import at.orchaldir.gm.core.selector.world.getRegions
import at.orchaldir.gm.core.selector.world.getRivers
import at.orchaldir.gm.core.selector.world.getStreets
import at.orchaldir.gm.visualization.settlement.getStreetTemplateFill
import at.orchaldir.gm.visualization.settlement.showTerrainName
import at.orchaldir.gm.visualization.settlement.visualizeSettlementMap
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlin.let

fun Application.configureSettlementMapRouting() {
    routing {
        get<SettlementMapRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                SettlementMapRoutes(),
                state.sortSettlementMaps(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Settlement") { tdLink(call, state, it.settlement) },
                    createStartDateColumn(call, state),
                    countColumnForId("Buildings", state::countBuildingsIn),
                    countColumnForId("Resident", state::countResidents),
                ),
            )
        }
        get<SettlementMapRoutes.Details> { details ->
            handleShowElementSplit(details.id, SettlementMapRoutes(), HtmlBlockTag::showSettlementDetails) { _, state, townMap ->
                svg(visualizeSettlementMapWithLinks(call, state, townMap), 90)
            }
        }
        get<SettlementMapRoutes.New> {
            handleCreateElement(SettlementMapRoutes(), STORE.getState().getSettlementMapStorage())
        }
        get<SettlementMapRoutes.Delete> { delete ->
            handleDeleteElement(SettlementMapRoutes(), delete.id)
        }
        get<SettlementMapRoutes.Edit> { edit ->
            handleEditElementSplit(
                edit.id,
                SettlementMapRoutes(),
                HtmlBlockTag::editSettlementMap,
                HtmlBlockTag::showSettlementMapEditorRight,
            )
        }
        post<SettlementMapRoutes.Preview> { preview ->
            handlePreviewElementSplit(
                preview.id,
                SettlementMapRoutes(),
                { state, parameters, id -> parseSettlementMap(state, parameters, state.getSettlementMapStorage().getOrThrow(id)) },
                HtmlBlockTag::editSettlementMap,
                HtmlBlockTag::showSettlementMapEditorRight,
            )
        }
        post<SettlementMapRoutes.Update> { update ->
            handleUpdateElement(update.id) { state, parameters, id ->
                val oldSettlementMap = state.getSettlementMapStorage().getOrThrow(id)

                parseSettlementMap(state, parameters, oldSettlementMap)
            }
        }
    }
}

private fun HtmlBlockTag.showSettlementDetails(
    call: ApplicationCall,
    state: State,
    settlementMap: SettlementMap,
) {
    val editAbstractBuildingsLink = call.application.href(AbstractBuildingRoutes.Edit(settlementMap.id))
    val editBuildingsLink = call.application.href(SettlementMapRoutes.BuildingRoutes.Edit(settlementMap.id))
    val editStreetsLink = call.application.href(SettlementMapRoutes.StreetRoutes.Edit(settlementMap.id))
    val editTerrainLink = call.application.href(SettlementMapRoutes.TerrainRoutes.Edit(settlementMap.id))

    showSettlementMap(call, state, settlementMap)

    action(editAbstractBuildingsLink, "Edit Abstract Buildings")
    action(editBuildingsLink, "Edit Buildings")

    h2 { +"Terrain" }

    fieldElements(call, state, state.getRegions(settlementMap.id))
    fieldElements(call, state, state.getRivers(settlementMap.id))
    fieldElements(call, state, state.getStreets(settlementMap.id))

    showStreetTemplateCount(call, state, settlementMap.id)

    action(editStreetsLink, "Edit Streets")
    action(editTerrainLink, "Edit Terrain")

    showLocalElements(call, state, settlementMap.id)
    showCharactersOfSettlementMap(call, state, settlementMap.settlement, settlementMap.id)

}

private fun HtmlBlockTag.showSettlementMapEditorRight(
    call: ApplicationCall,
    state: State,
    settlementMap: SettlementMap,
) {
    svg(visualizeSettlementMapWithLinks(call, state, settlementMap), 90)
}

private fun visualizeSettlementMapWithLinks(
    call: ApplicationCall,
    state: State,
    settlement: SettlementMap,
) = visualizeSettlementMap(
    settlement,
    state.getBuildingsIn(settlement.id),
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
