package at.orchaldir.gm.app.routes.world.town

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.routes.world.BuildingRoutes
import at.orchaldir.gm.app.routes.world.StreetRoutes
import at.orchaldir.gm.app.routes.world.town.TownMapRoutes.AbstractBuildingRoutes
import at.orchaldir.gm.core.action.CreateTownMap
import at.orchaldir.gm.core.action.DeleteTownMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.selector.character.countResident
import at.orchaldir.gm.core.selector.world.*
import at.orchaldir.gm.visualization.town.getStreetTemplateFill
import at.orchaldir.gm.visualization.town.showTerrainName
import at.orchaldir.gm.visualization.town.visualizeTown
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

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
            logger.info { "Get details of town map ${details.id.value}" }

            val state = STORE.getState()
            val town = state.getTownMapStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTownMapDetails(call, state, town)
            }
        }
        get<TownMapRoutes.New> {
            logger.info { "Add new town map" }

            STORE.dispatch(CreateTownMap)

            val id = STORE.getState().getTownMapStorage().lastId
            call.respondRedirect(call.application.href(TownMapRoutes.Details(id)))

            STORE.getState().save()
        }
        get<TownMapRoutes.Delete> { delete ->
            logger.info { "Delete town map ${delete.id.value}" }

            STORE.dispatch(DeleteTownMap(delete.id))

            call.respondRedirect(call.application.href(TownMapRoutes.All()))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllTownMaps(
    call: ApplicationCall,
    state: State,
) {
    val townMaps = state.getTownMapStorage().getAll()
    val createLink = call.application.href(TownMapRoutes.New())

    simpleHtml("Town Maps") {
        field("Count", townMaps.size)
        table {
            tr {
                th { +"Name" }
                th { +"Buildings" }
                th { +"Residents" }
            }
            townMaps.forEach { town ->
                tr {
                    tdLink(call, state, town)
                    tdSkipZero(state.countBuildings(town.id))
                    tdSkipZero(state.countResident(town.id))
                }
            }
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showTownMapDetails(
    call: ApplicationCall,
    state: State,
    town: TownMap,
) {
    val backLink = call.application.href(TownMapRoutes.All())
    val deleteLink = call.application.href(TownMapRoutes.Delete(town.id))
    val editAbstractBuildingsLink = call.application.href(AbstractBuildingRoutes.Edit(town.id))
    val editBuildingsLink = call.application.href(TownMapRoutes.BuildingRoutes.Edit(town.id))
    val editStreetsLink = call.application.href(TownMapRoutes.StreetRoutes.Edit(town.id))
    val editTerrainLink = call.application.href(TownMapRoutes.TerrainRoutes.Edit(town.id))

    simpleHtmlDetails(town) {
        split({
            field("Size", town.map.size.format())

            action(editAbstractBuildingsLink, "Edit Abstract Buildings")
            action(editBuildingsLink, "Edit Buildings")

            if (state.canDeleteTownMap(town.id)) {
                action(deleteLink, "Delete")
            }

            h2 { +"Terrain" }

            fieldList(call, state, state.getMountains(town.id).sortedBy { it.name.text })
            fieldList(call, state, state.getRivers(town.id).sortedBy { it.name.text })
            fieldList(call, state, state.getStreets(town.id).sortedBy { it.name(state) })

            showStreetTemplateCount(call, state, town.id)

            action(editStreetsLink, "Edit Streets")
            action(editTerrainLink, "Edit Terrain")

            back(backLink)
        }, {
            svg(visualizeTownWithLinks(call, state, town), 90)
        })
    }
}


private fun visualizeTownWithLinks(
    call: ApplicationCall,
    state: State,
    town: TownMap,
) = visualizeTown(
    town, state.getBuildings(town.id),
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
