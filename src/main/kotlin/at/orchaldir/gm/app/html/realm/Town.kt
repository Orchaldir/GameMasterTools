package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.OWNER
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.displayIncome
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.math.parseAreaLookup
import at.orchaldir.gm.app.html.util.math.selectAreaLookup
import at.orchaldir.gm.app.html.realm.population.editPopulation
import at.orchaldir.gm.app.html.realm.population.parsePopulation
import at.orchaldir.gm.app.html.realm.population.showAreaAndPopulation
import at.orchaldir.gm.app.html.realm.population.showPopulationDetails
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.html.world.showCharactersOfTownMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.realm.population.Population
import at.orchaldir.gm.core.selector.realm.getDistricts
import at.orchaldir.gm.core.selector.realm.getExistingRealms
import at.orchaldir.gm.core.selector.realm.getRealmsWithCapital
import at.orchaldir.gm.core.selector.realm.getRealmsWithPreviousCapital
import at.orchaldir.gm.core.selector.util.sortDistricts
import at.orchaldir.gm.core.selector.world.getCurrentTownMap
import at.orchaldir.gm.core.selector.world.getTownMaps
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun HtmlBlockTag.showTown(
    call: ApplicationCall,
    state: State,
    town: Town,
) {
    optionalField("Title", town.title)
    fieldReference(call, state, town.founder, "Founder")
    optionalField(call, state, "Founding Date", town.date)
    showVitalStatus(call, state, town.status)
    showHistory(call, state, town.owner, "Owner", "Independent") { _, _, owner ->
        link(call, state, owner)
    }
    fieldElements(call, state, "Capital of", state.getRealmsWithCapital(town.id))
    fieldElements(call, state, "Previous Capital of", state.getRealmsWithPreviousCapital(town.id))
    showAreaAndPopulation(call, state, town)
    showSubDistricts(call, state, state.getDistricts(town.id), town.population)
    showDataSources(call, state, town.sources)

    val currentTownMap = state.getCurrentTownMap(town.id)

    if (currentTownMap != null) {
        optionalFieldLink(call, state, currentTownMap.id)
        fieldElements(call, state, "Previous Town Maps", state.getTownMaps(town.id) - currentTownMap)
        showLocalElements(call, state, town, currentTownMap)
    } else {
        showLocalElements(call, state, town.id)
    }
    showCharactersOfTownMap(call, state, town.id, currentTownMap?.id)

    showCreated(call, state, town.id)
    showOwnedElements(call, state, town.id)
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

    val townPopulation = population.getTotalPopulation() ?: 0

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

        val missing = townPopulation - totalPopulation

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

fun HtmlBlockTag.editTown(
    call: ApplicationCall,
    state: State,
    town: Town,
) {
    selectName(town.name)
    selectOptionalNotEmptyString("Optional Title", town.title, TITLE)
    selectCreator(state, town.founder, town.id, town.date, "Founder")
    selectOptionalDate(state, "Founding Date", town.date, DATE)
    selectVitalStatus(
        state,
        town.id,
        town.date,
        town.status,
        ALLOWED_VITAL_STATUS_FOR_TOWN,
        ALLOWED_CAUSES_OF_DEATH_FOR_TOWN,
    )
    selectHistory(state, OWNER, town.owner, "Owner", town.date) { _, param, owner, start ->
        selectOptionalElement(
            state,
            "Realm",
            param,
            state.getExistingRealms(start),
            owner,
        )
    }
    selectAreaLookup(town.area, state.data.largeAreaUnit)
    editPopulation(call, state, town.population)
    editDataSources(state, town.sources)
}

// parse

fun parseTownId(parameters: Parameters, param: String) = TownId(parseInt(parameters, param))
fun parseOptionalTownId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { TownId(it) }

fun parseTown(
    state: State,
    parameters: Parameters,
    id: TownId,
): Town {
    val date = parseOptionalDate(parameters, state, DATE)

    return Town(
        id,
        parseName(parameters),
        parseOptionalNotEmptyString(parameters, TITLE),
        parseCreator(parameters),
        date,
        parseVitalStatus(parameters, state),
        parseHistory(parameters, OWNER, state, date) { _, _, param ->
            parseOptionalRealmId(parameters, param)
        },
        parseAreaLookup(parameters, state.data.largeAreaUnit),
        parsePopulation(parameters, state),
        parseDataSources(parameters),
    )
}
