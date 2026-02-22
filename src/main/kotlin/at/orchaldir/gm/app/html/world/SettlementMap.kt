package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.TERRAIN
import at.orchaldir.gm.app.SETTLEMENT
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.parseOptionalSettlementId
import at.orchaldir.gm.app.html.util.optionalField
import at.orchaldir.gm.app.html.util.parseOptionalDate
import at.orchaldir.gm.app.html.util.selectOptionalDate
import at.orchaldir.gm.app.html.util.showEmployees
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.realm.SettlementId
import at.orchaldir.gm.core.model.world.settlement.TerrainType
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.model.world.settlement.SettlementMapId
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getResidents
import at.orchaldir.gm.core.selector.character.getWorkingIn
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showSettlementMap(
    call: ApplicationCall,
    state: State,
    settlementMap: SettlementMap,
) {
    optionalFieldLink(call, state, settlementMap.settlement)
    optionalField(call, state, "Date", settlementMap.date)
    field("Size", settlementMap.map.size.format())
}

fun HtmlBlockTag.showCharactersOfSettlementMap(
    call: ApplicationCall,
    state: State,
    settlement: SettlementId?,
    settlementMap: SettlementMapId?,
) {
    val employees = if (settlement != null) {
        state.getEmployees(settlement)
    } else {
        emptyList()
    }
    val residents = state.getResidents(settlement, settlementMap)
    val workers = if (settlementMap != null) {
        state.getWorkingIn(settlementMap) - residents
    } else {
        emptyList()
    }

    showCharactersOfSettlementMap(
        call,
        state,
        employees,
        residents,
        workers,
    )
}

private fun HtmlBlockTag.showCharactersOfSettlementMap(
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

    showEmployees(call, state, employees, showSettlement = false)
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

fun HtmlBlockTag.editSettlementMap(
    call: ApplicationCall,
    state: State,
    settlementMap: SettlementMap,
) {
    selectOptionalElement(state, "Settlement", SETTLEMENT, state.getSettlementStorage().getAll(), settlementMap.settlement)
    selectOptionalDate(state, "Date", settlementMap.date, DATE)
}

// parse

fun parseSettlementMapId(parameters: Parameters, param: String) = SettlementMapId(parseInt(parameters, param))
fun parseOptionalSettlementMapId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { SettlementMapId(it) }

fun parseTerrainType(parameters: Parameters) = parse(parameters, combine(TERRAIN, TYPE), TerrainType.Plain)

fun parseSettlementMap(state: State, parameters: Parameters, oldSettlementMap: SettlementMap) = oldSettlementMap.copy(
    settlement = parseOptionalSettlementId(parameters, SETTLEMENT),
    date = parseOptionalDate(parameters, state, DATE),
)