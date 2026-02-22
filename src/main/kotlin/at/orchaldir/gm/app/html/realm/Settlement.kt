package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.OWNER
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.displayIncome
import at.orchaldir.gm.app.html.economy.editEconomy
import at.orchaldir.gm.app.html.economy.parseEconomy
import at.orchaldir.gm.app.html.economy.showEconomyDetails
import at.orchaldir.gm.app.html.realm.population.editPopulation
import at.orchaldir.gm.app.html.realm.population.parsePopulation
import at.orchaldir.gm.app.html.realm.population.showAreaAndPopulation
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.math.parseAreaLookup
import at.orchaldir.gm.app.html.util.math.selectAreaLookup
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.html.world.showCharactersOfSettlementMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.realm.population.Population
import at.orchaldir.gm.core.selector.realm.getDistricts
import at.orchaldir.gm.core.selector.realm.getExistingRealms
import at.orchaldir.gm.core.selector.realm.getRealmsWithCapital
import at.orchaldir.gm.core.selector.realm.getRealmsWithPreviousCapital
import at.orchaldir.gm.core.selector.util.sortDistricts
import at.orchaldir.gm.core.selector.world.getCurrentSettlementMap
import at.orchaldir.gm.core.selector.world.getSettlementMaps
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showSettlement(
    call: ApplicationCall,
    state: State,
    settlement: Settlement,
) {
    optionalField("Title", settlement.title)
    fieldReference(call, state, settlement.founder, "Founder")
    optionalField(call, state, "Founding Date", settlement.date)
    showVitalStatus(call, state, settlement.status)
    showHistory(call, state, settlement.owner, "Owner", "Independent") { _, _, owner ->
        link(call, state, owner)
    }
    fieldElements(call, state, "Capital of", state.getRealmsWithCapital(settlement.id))
    fieldElements(call, state, "Previous Capital of", state.getRealmsWithPreviousCapital(settlement.id))
    showAreaAndPopulation(call, state, settlement)
    showEconomyDetails(call, state, settlement)
    showSubDistricts(call, state, state.getDistricts(settlement.id), settlement.population)
    showDataSources(call, state, settlement.sources)

    val currentMap = state.getCurrentSettlementMap(settlement.id)

    if (currentMap != null) {
        optionalFieldLink(call, state, currentMap.id)
        fieldElements(call, state, "Previous Settlement Maps", state.getSettlementMaps(settlement.id) - currentMap)
        showLocalElements(call, state, settlement, currentMap)
    } else {
        showLocalElements(call, state, settlement.id)
    }
    showCharactersOfSettlementMap(call, state, settlement.id, currentMap?.id)

    showCreated(call, state, settlement.id)
    showOwnedElements(call, state, settlement.id)
}

fun HtmlBlockTag.showSubDistricts(
    call: ApplicationCall,
    state: State,
    districts: List<District>,
    population: Population,
) {
    var totalPopulation = 0

    if (districts.isEmpty()) {
        return
    }

    val settlementPopulation = population.getTotalPopulation() ?: 0

    br { }
    table {
        tr {
            th { +"Districts" }
            th { +"Population" }
            th { +"Income" }
        }
        state
            .sortDistricts(districts)
            .forEach { district ->
                val districtPopulation = district.population.getTotalPopulation() ?: 0

                tr {
                    tdLink(call, state, district.id)
                    tdSkipZero(districtPopulation)
                    td {
                        district.population.income()?.let { displayIncome(call, state, it) }
                    }
                }

                totalPopulation += districtPopulation
            }

        tr {
            tdString("Total")
            tdSkipZero(totalPopulation)
        }

        val missing = settlementPopulation - totalPopulation

        if (missing != 0) {
            tr {
                tdString("Missing")
                tdSkipZero(missing)
            }
        }
    }
    br { }
}

// edit

fun HtmlBlockTag.editSettlement(
    call: ApplicationCall,
    state: State,
    settlement: Settlement,
) {
    selectName(settlement.name)
    selectOptionalNotEmptyString("Optional Title", settlement.title, TITLE)
    selectCreator(state, settlement.founder, settlement.id, settlement.date, "Founder")
    selectOptionalDate(state, "Founding Date", settlement.date, DATE)
    selectVitalStatus(
        state,
        settlement.id,
        settlement.date,
        settlement.status,
        ALLOWED_VITAL_STATUS_FOR_SETTLEMENT,
        ALLOWED_CAUSES_OF_DEATH_FOR_SETTLEMENT,
    )
    selectHistory(state, OWNER, settlement.owner, "Owner", settlement.date) { _, param, owner, start ->
        selectOptionalElement(
            state,
            "Realm",
            param,
            state.getExistingRealms(start),
            owner,
        )
    }
    selectAreaLookup(settlement.area, state.config.largeAreaUnit)
    editPopulation(call, state, settlement.population)
    editEconomy(call, state, settlement.economy)
    editDataSources(state, settlement.sources)
}

// parse

fun parseSettlementId(parameters: Parameters, param: String) = SettlementId(parseInt(parameters, param))
fun parseOptionalSettlementId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { SettlementId(it) }

fun parseSettlement(
    state: State,
    parameters: Parameters,
    id: SettlementId,
): Settlement {
    val date = parseOptionalDate(parameters, state, DATE)

    return Settlement(
        id,
        parseName(parameters),
        parseOptionalNotEmptyString(parameters, TITLE),
        parseCreator(parameters),
        date,
        parseVitalStatus(parameters, state),
        parseHistory(parameters, OWNER, state, date) { _, _, param ->
            parseOptionalRealmId(parameters, param)
        },
        parseAreaLookup(parameters, state.config.largeAreaUnit),
        parsePopulation(parameters, state),
        parseEconomy(parameters, state),
        parseDataSources(parameters),
    )
}
