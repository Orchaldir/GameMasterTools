package at.orchaldir.gm.app.routes.world.town

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.world.parseTown
import at.orchaldir.gm.app.routes.world.BuildingRoutes
import at.orchaldir.gm.app.routes.world.StreetRoutes
import at.orchaldir.gm.core.action.CreateTown
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.selector.world.*
import at.orchaldir.gm.visualization.town.getStreetTypeFill
import at.orchaldir.gm.visualization.town.showTerrainName
import at.orchaldir.gm.visualization.town.visualizeTown
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.DIV
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.form
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureTownRouting() {
    routing {
        get<TownRoutes> {
            logger.info { "Get all towns" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllTowns(call)
            }
        }
        get<TownRoutes.Details> { details ->
            logger.info { "Get details of town ${details.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTownDetails(call, state, town)
            }
        }
        get<TownRoutes.New> {
            logger.info { "Add new town" }

            STORE.dispatch(CreateTown)

            call.respondRedirect(call.application.href(TownRoutes.Edit(STORE.getState().getTownStorage().lastId)))

            STORE.getState().save()
        }
        get<TownRoutes.Delete> { delete ->
            logger.info { "Delete town ${delete.id.value}" }

            STORE.dispatch(DeleteTown(delete.id))

            call.respondRedirect(call.application.href(TownRoutes()))

            STORE.getState().save()
        }
        get<TownRoutes.Edit> { edit ->
            logger.info { "Get editor for town ${edit.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTownEditor(call, state, town)
            }
        }
        post<TownRoutes.Update> { update ->
            logger.info { "Update town ${update.id.value}" }

            val state = STORE.getState()
            val oldTown = state.getTownStorage().getOrThrow(update.id)
            val town = parseTown(call.receiveParameters(), state, oldTown)

            STORE.dispatch(UpdateTown(town))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllTowns(call: ApplicationCall) {
    val towns = STORE.getState().getTownStorage().getAll().sortedBy { it.name }
    val count = towns.size
    val createLink = call.application.href(TownRoutes.New())

    simpleHtml("Towns") {
        field("Count", count.toString())
        showList(towns) { nameList ->
            link(call, nameList)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showTownDetails(
    call: ApplicationCall,
    state: State,
    town: Town,
) {
    val buildings = state.getBuildings(town.id)
    val backLink = call.application.href(TownRoutes())
    val deleteLink = call.application.href(TownRoutes.Delete(town.id))
    val editLink = call.application.href(TownRoutes.Edit(town.id))
    val editBuildingsLink = call.application.href(TownRoutes.BuildingRoutes.Edit(town.id))
    val editStreetsLink = call.application.href(TownRoutes.StreetRoutes.Edit(town.id))
    val editTerrainLink = call.application.href(TownRoutes.TerrainRoutes.Edit(town.id))

    simpleHtml("Town: ${town.name}") {
        split({
            field("Id", town.id.value.toString())
            field("Name", town.name)
            field(call, state, "Founding", town.foundingDate)
            fieldAge("Age", state.getAgeInYears(town))
            field("Size", town.map.size.format())
            showArchitecturalStyles(call, state, buildings)
            showList("Buildings", buildings.sortedBy { it.name }) { building ->
                link(call, building)
            }
            showList("Mountains", state.getMountains(town.id).sortedBy { it.name }) { mountain ->
                link(call, mountain)
            }
            showList("Rivers", state.getRivers(town.id).sortedBy { it.name }) { river ->
                link(call, river)
            }
            showList("Streets", state.getStreets(town.id).sortedBy { it.name }) { street ->
                link(call, street)
            }
            action(editLink, "Edit Town")
            action(editBuildingsLink, "Edit Buildings")
            action(editStreetsLink, "Edit Streets")
            action(editTerrainLink, "Edit Terrain")
            action(deleteLink, "Delete")
            back(backLink)
        }, {
            svg(visualizeTownWithLinks(call, state, town), 90)
        })
    }
}

fun HtmlBlockTag.showArchitecturalStyles(
    call: ApplicationCall,
    state: State,
    buildings: List<Building>,
) {
    showMap("Architectural Styles", countArchitecturalStyles(buildings)) { style, count ->
        link(call, state, style)
        +": $count"
    }
}

private fun HTML.showTownEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
) {
    val backLink = href(call, town.id)
    val updateLink = call.application.href(TownRoutes.Update(town.id))

    simpleHtml("Edit Town: ${town.name}") {
        split({
            field("Id", town.id.value.toString())
            form {
                selectName(town.name)
                selectDate(state, "Founding", town.foundingDate, DATE)
                button("Update", updateLink)
            }
            back(backLink)
        }, {
            svg(visualizeTownWithLinks(call, state, town), 90)
        })
    }
}

private fun visualizeTownWithLinks(
    call: ApplicationCall,
    state: State,
    town: Town,
) = visualizeTown(
    town, state.getBuildings(town.id),
    tileTooltipLookup = showTerrainName(state),
    buildingLinkLookup = { building ->
        call.application.href(BuildingRoutes.Details(building.id))
    },
    buildingTooltipLookup = { building ->
        building.name
    },
    streetLinkLookup = { street, _ ->
        call.application.href(StreetRoutes.Details(street))
    },
    streetTooltipLookup = { streetId, _ ->
        state.getStreetStorage().getOrThrow(streetId).name
    },
    streetColorLookup = getStreetTypeFill(state),
)
