package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.TERRAIN
import at.orchaldir.gm.app.TOWN
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.parseOptionalTownId
import at.orchaldir.gm.app.html.util.optionalField
import at.orchaldir.gm.app.html.util.parseOptionalDate
import at.orchaldir.gm.app.html.util.selectOptionalDate
import at.orchaldir.gm.app.html.util.showEmployees
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.world.town.TerrainType
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getResidents
import at.orchaldir.gm.core.selector.character.getWorkingIn
import at.orchaldir.gm.core.selector.util.sortBuildings
import at.orchaldir.gm.core.selector.util.sortCharacters
import at.orchaldir.gm.core.selector.util.sortTowns
import at.orchaldir.gm.core.selector.world.getBuildings
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showTownMap(
    call: ApplicationCall,
    state: State,
    townMap: TownMap,
) {
    optionalFieldLink(call, state, townMap.town)
    optionalField(call, state, "Date", townMap.date)
    field("Size", townMap.map.size.format())
}

fun HtmlBlockTag.showBuildingsOfTownMap(
    call: ApplicationCall,
    state: State,
    townMap: TownMapId,
    town: TownId? = null,
) {
    val buildingsInMap = state.getBuildings(townMap)
    val buildingsInTown = if (town != null) {
        state.getBuildings(town)
    } else {
        emptyList()
    }
    val buildings = buildingsInMap + buildingsInTown

    h2 { +"Buildings" }

    showDetails("Buildings in Map") {
        showList(state.sortBuildings(buildingsInMap)) { (building, name) ->
            link(call, building.id, name)
        }
    }
    if (town != null) {
        showDetails("Buildings in Town") {
            showList(state.sortBuildings(buildingsInTown)) { (building, name) ->
                link(call, building.id, name)
            }
        }
    }

    showArchitecturalStyleCount(call, state, buildings)
    showCreatorCount(call, state, buildings, "Builder")
    showBuildingPurposeCount(buildings)
    showBuildingOwnershipCount(call, state, buildings)
}

fun HtmlBlockTag.showCharactersOfTownMap(
    call: ApplicationCall,
    state: State,
    town: TownId?,
    townMap: TownMapId?,
) {
    val employees = if (town != null) {
        state.getEmployees(town)
    } else {
        emptyList()
    }
    val residents = state.getResidents(town, townMap)
    val workers = if (townMap != null) {
        state.getWorkingIn(townMap) - residents
    } else {
        emptyList()
    }

    showCharactersOfTownMap(
        call,
        state,
        employees,
        residents,
        workers,
    )
}

private fun HtmlBlockTag.showCharactersOfTownMap(
    call: ApplicationCall,
    state: State,
    employees: List<Character>,
    residents: List<Character>,
    workers: List<Character>,
) {
    if (employees.isEmpty() && residents.isEmpty() && workers.isEmpty()) {
        return
    }

    h2 { +"Characters" }

    showEmployees(call, state, employees, showTown = false)
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
}

// edit

fun FORM.editTownMap(
    state: State,
    townMap: TownMap,
) {
    selectOptionalElement(state, "Town", TOWN, state.sortTowns(), townMap.town)
    selectOptionalDate(state, "Date", townMap.date, DATE)
}

// parse
fun parseTownMapId(parameters: Parameters, param: String) = TownMapId(parseInt(parameters, param))
fun parseOptionalTownMapId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { TownMapId(it) }

fun parseTerrainType(parameters: Parameters) = parse(parameters, combine(TERRAIN, TYPE), TerrainType.Plain)

fun parseTownMap(parameters: Parameters, state: State, oldTownMap: TownMap) = oldTownMap.copy(
    town = parseOptionalTownId(parameters, TOWN),
    date = parseOptionalDate(parameters, state, DATE),
)