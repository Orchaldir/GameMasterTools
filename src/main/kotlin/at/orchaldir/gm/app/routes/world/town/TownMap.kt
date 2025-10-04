package at.orchaldir.gm.app.routes.world.town

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.showLocalElements
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.world.editTownMap
import at.orchaldir.gm.app.html.world.parseTownMap
import at.orchaldir.gm.app.html.world.showCharactersOfTownMap
import at.orchaldir.gm.app.html.world.showTownMap
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElementSplit
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.world.BuildingRoutes
import at.orchaldir.gm.app.routes.world.StreetRoutes
import at.orchaldir.gm.app.routes.world.town.TownMapRoutes.AbstractBuildingRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortTownMap
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.selector.character.countResident
import at.orchaldir.gm.core.selector.util.countBuildingsIn
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.core.selector.util.sortTownMaps
import at.orchaldir.gm.core.selector.world.getRegions
import at.orchaldir.gm.core.selector.world.getRivers
import at.orchaldir.gm.core.selector.world.getStreets
import at.orchaldir.gm.visualization.town.getStreetTemplateFill
import at.orchaldir.gm.visualization.town.showTerrainName
import at.orchaldir.gm.visualization.town.visualizeTown
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging
import kotlin.let

private val logger = KotlinLogging.logger {}

fun Application.configureTownMapRouting() {
    routing {
        get<TownMapRoutes.All> { all ->
            logger.info { "Get all town maps" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllTownMaps(call, STORE.getState())
            }
        }
        get<TownMapRoutes.Details> { details ->
            handleShowElementSplit(details.id, TownMapRoutes(), HtmlBlockTag::showTownMapDetails) { _, state, townMap ->
                svg(visualizeTownWithLinks(call, state, townMap), 90)
            }
        }
        get<TownMapRoutes.New> {
            handleCreateElement(STORE.getState().getTownMapStorage()) { id ->
                TownMapRoutes.Edit(id)
            }
        }
        get<TownMapRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, TownMapRoutes.All())
        }
        get<TownMapRoutes.Edit> { edit ->
            logger.info { "Get editor for town map ${edit.id.value}" }

            val state = STORE.getState()
            val townMap = state.getTownMapStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTownMapEditor(call, state, townMap)
            }
        }
        post<TownMapRoutes.Preview> { preview ->
            logger.info { "Preview town map ${preview.id.value}" }

            val state = STORE.getState()
            val oldTownMap = state.getTownMapStorage().getOrThrow(preview.id)
            val townMap = parseTownMap(state, call.receiveParameters(), oldTownMap)

            call.respondHtml(HttpStatusCode.OK) {
                showTownMapEditor(call, state, townMap)
            }
        }
        post<TownMapRoutes.Update> { update ->
            handleUpdateElement(update.id) { state, parameters, id ->
                val oldTownMap = state.getTownMapStorage().getOrThrow(id)

                parseTownMap(state, parameters, oldTownMap)
            }
        }
    }
}

private fun HTML.showAllTownMaps(
    call: ApplicationCall,
    state: State,
    sort: SortTownMap = SortTownMap.Name,
) {
    val townMaps = state.sortTownMaps(sort)
    val createLink = call.application.href(TownMapRoutes.New())

    simpleHtml("Town Maps") {
        field("Count", townMaps.size)
        table {
            tr {
                th { +"Name" }
                th { +"Town" }
                th { +"Date" }
                th { +"Buildings" }
                th { +"Residents" }
            }
            townMaps.forEach { townMap ->
                tr {
                    tdLink(call, state, townMap)
                    tdLink(call, state, townMap.town)
                    td { showOptionalDate(call, state, townMap.date) }
                    tdSkipZero(state.countBuildingsIn(townMap.id))
                    tdSkipZero(state.countResident(townMap.id))
                }
            }
        }
        action(createLink, "Add")
        back("/")
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

private fun HTML.showTownMapEditor(
    call: ApplicationCall,
    state: State,
    townMap: TownMap,
) {
    val backLink = href(call, townMap.id)
    val previewLink = call.application.href(TownMapRoutes.Preview(townMap.id))
    val updateLink = call.application.href(TownMapRoutes.Update(townMap.id))

    simpleHtml("Edit Town Map ${townMap.name(state)}") {
        split({
            formWithPreview(previewLink, updateLink, backLink) {
                editTownMap(state, townMap)
            }
        }, {
            svg(visualizeTownWithLinks(call, state, townMap), 90)
        })
    }
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
