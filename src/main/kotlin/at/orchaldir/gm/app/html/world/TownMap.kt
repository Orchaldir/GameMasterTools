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
import io.ktor.http.*
import io.ktor.server.application.*
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
    fieldElements(call, state, "Residents", residents)
    fieldElements(call, state, "Workers, but not Residents", workers)

    val characters = residents.toSet() + workers.toSet()

    showCauseOfDeath(characters)
    showCultureCount(call, state, characters)
    showGenderCount(characters)
    showJobCount(call, state, characters)
    showHousingStatusCount(characters)
    showRaceCount(call, state, characters)
}

// edit

fun HtmlBlockTag.editTownMap(
    call: ApplicationCall,
    state: State,
    townMap: TownMap,
) {
    selectOptionalElement(state, "Town", TOWN, state.getTownStorage().getAll(), townMap.town)
    selectOptionalDate(state, "Date", townMap.date, DATE)
}

// parse
fun parseTownMapId(parameters: Parameters, param: String) = TownMapId(parseInt(parameters, param))
fun parseOptionalTownMapId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { TownMapId(it) }

fun parseTerrainType(parameters: Parameters) = parse(parameters, combine(TERRAIN, TYPE), TerrainType.Plain)

fun parseTownMap(state: State, parameters: Parameters, oldTownMap: TownMap) = oldTownMap.copy(
    town = parseOptionalTownId(parameters, TOWN),
    date = parseOptionalDate(parameters, state, DATE),
)