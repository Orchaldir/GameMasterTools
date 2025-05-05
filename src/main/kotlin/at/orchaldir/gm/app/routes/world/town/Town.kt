package at.orchaldir.gm.app.routes.world.town

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.world.parseTown
import at.orchaldir.gm.app.routes.world.BuildingRoutes
import at.orchaldir.gm.app.routes.world.StreetRoutes
import at.orchaldir.gm.core.action.CreateTown
import at.orchaldir.gm.core.action.DeleteTown
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortTown
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.selector.character.countResident
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getResident
import at.orchaldir.gm.core.selector.character.getWorkingIn
import at.orchaldir.gm.core.selector.util.sortBuildings
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.core.selector.util.sortTowns
import at.orchaldir.gm.core.selector.world.*
import at.orchaldir.gm.visualization.town.getStreetTemplateFill
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
import kotlinx.html.HTML
import kotlinx.html.h2
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureTownRouting() {
    routing {
        get<TownRoutes.All> { all ->
            logger.info { "Get all towns" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllTowns(call, STORE.getState(), all.sort)
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

            call.respondRedirect(call.application.href(TownRoutes.All()))

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
        post<TownRoutes.Preview> { preview ->
            logger.info { "Preview town ${preview.id.value}" }

            val state = STORE.getState()
            val oldTown = state.getTownStorage().getOrThrow(preview.id)
            val town = parseTown(call.receiveParameters(), state, oldTown)

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

private fun HTML.showAllTowns(
    call: ApplicationCall,
    state: State,
    sort: SortTown,
) {
    val towns = state.sortTowns(sort)
    val createLink = call.application.href(TownRoutes.New())

    simpleHtml("Towns") {
        field("Count", towns.size)
        showSortTableLinks(call, SortTown.entries, TownRoutes(), TownRoutes::All)
        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Founder" }
                th { +"Buildings" }
                th { +"Residents" }
            }
            towns.forEach { town ->
                tr {
                    td { link(call, town) }
                    td { showDate(call, state, town.startDate()) }
                    td { showCreator(call, state, town.founder, false) }
                    tdSkipZero(state.countBuildings(town.id))
                    tdSkipZero(state.countResident(town.id))
                }
            }
        }
        showCreatorCount(call, state, towns, "Founders")
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
    val backLink = call.application.href(TownRoutes.All())
    val deleteLink = call.application.href(TownRoutes.Delete(town.id))
    val editLink = call.application.href(TownRoutes.Edit(town.id))
    val editBuildingsLink = call.application.href(TownRoutes.BuildingRoutes.Edit(town.id))
    val editStreetsLink = call.application.href(TownRoutes.StreetRoutes.Edit(town.id))
    val editTerrainLink = call.application.href(TownRoutes.TerrainRoutes.Edit(town.id))

    simpleHtmlDetails(town) {
        split({
            fieldName(town.name)
            field(call, state, "Founding", town.foundingDate)
            fieldAge("Age", state.getAgeInYears(town))
            fieldCreator(call, state, town.founder, "Founder")
            field("Size", town.map.size.format())
            action(editLink, "Edit Town")
            if (state.canDelete(town.id)) {
                action(deleteLink, "Delete")
            }
            h2 { +"Buildings" }
            showArchitecturalStyleCount(call, state, buildings)
            showCreatorCount(call, state, buildings, "Builder")
            showBuildingPurposeCount(buildings)
            showDetails("Buildings") {
                showList(state.sortBuildings(buildings)) { (building, name) ->
                    link(call, building.id, name)
                }
            }
            showBuildingOwnershipCount(call, state, buildings)
            action(editBuildingsLink, "Edit Buildings")
            h2 { +"Characters" }
            val employees = state.getEmployees(town.id)
            val residents = state.getResident(town.id)
            val workers = state.getWorkingIn(town.id) - residents
            fieldList("Employees", state.sortCharacters(employees)) { employee ->
                link(call, state, employee)
                +" as "
                showEmploymentStatus(call, state, employee.employmentStatus.current, showTown = false)
            }
            fieldList(call, state, "Residents", state.sortCharacters(residents))
            fieldList(call, state, "Workers, but not Residents", state.sortCharacters(workers))
            val characters = residents.toSet() + workers.toSet()
            showCauseOfDeath(characters)
            showCultureCount(call, state, characters)
            showGenderCount(characters)
            showJobCount(call, state, characters)
            showHousingStatusCount(characters)
            showPersonalityCountForCharacters(call, state, characters)
            showRaceCount(call, state, characters)
            h2 { +"Terrain" }
            fieldList(call, state, state.getMountains(town.id).sortedBy { it.name.text })
            fieldList(call, state, state.getRivers(town.id).sortedBy { it.name.text })
            fieldList(call, state, state.getStreets(town.id).sortedBy { it.name(state) })
            showStreetTemplateCount(call, state, town.id)
            action(editStreetsLink, "Edit Streets")
            action(editTerrainLink, "Edit Terrain")

            showOwnedElements(call, state, town.id)
            showCreated(call, state, town.id)

            back(backLink)
        }, {
            svg(visualizeTownWithLinks(call, state, town), 90)
        })
    }
}


private fun HTML.showTownEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
) {
    val backLink = href(call, town.id)
    val previewLink = call.application.href(TownRoutes.Preview(town.id))
    val updateLink = call.application.href(TownRoutes.Update(town.id))

    simpleHtmlEditor(town) {
        split({
            formWithPreview(previewLink, updateLink, backLink) {
                selectName(town.name)
                selectDate(state, "Founding", town.foundingDate, DATE)
                selectCreator(state, town.founder, town.id, town.foundingDate, "Founder")
            }
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
