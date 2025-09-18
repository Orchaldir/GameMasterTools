package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.OWNER
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.population.editPopulation
import at.orchaldir.gm.app.html.util.population.parsePopulation
import at.orchaldir.gm.app.html.util.population.showPopulation
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.html.world.showCharactersOfTownMap
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.util.VALID_CAUSES_FOR_TOWNS
import at.orchaldir.gm.core.model.util.VALID_VITAL_STATUS_FOR_TOWNS
import at.orchaldir.gm.core.selector.realm.getDistricts
import at.orchaldir.gm.core.selector.realm.getExistingRealms
import at.orchaldir.gm.core.selector.realm.getRealmsWithCapital
import at.orchaldir.gm.core.selector.realm.getRealmsWithPreviousCapital
import at.orchaldir.gm.core.selector.world.getCurrentTownMap
import at.orchaldir.gm.core.selector.world.getTownMaps
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTown(
    call: ApplicationCall,
    state: State,
    town: Town,
) {
    optionalField("Title", town.title)
    fieldReference(call, state, town.founder, "Founder")
    optionalField(call, state, "Founding Date", town.foundingDate)
    showVitalStatus(call, state, town.status)
    showHistory(call, state, town.owner, "Owner", "Independent") { _, _, owner ->
        link(call, state, owner)
    }
    fieldElements(call, state, "Capital of", state.getRealmsWithCapital(town.id))
    fieldElements(call, state, "Previous Capital of", state.getRealmsWithPreviousCapital(town.id))
    showPopulation(call, state, town)
    fieldElements(call, state, "Districts", state.getDistricts(town.id))
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

// edit

fun FORM.editTown(
    call: ApplicationCall,
    state: State,
    town: Town,
) {
    selectName(town.name)
    selectOptionalNotEmptyString("Optional Title", town.title, TITLE)
    selectCreator(state, town.founder, town.id, town.foundingDate, "Founder")
    selectOptionalDate(state, "Founding Date", town.foundingDate, DATE)
    selectVitalStatus(
        state,
        town.id,
        town.foundingDate,
        town.status,
        VALID_VITAL_STATUS_FOR_TOWNS,
        VALID_CAUSES_FOR_TOWNS,
    )
    selectHistory(state, OWNER, town.owner, "Owner", town.foundingDate) { _, param, owner, start ->
        selectOptionalElement(
            state,
            "Realm",
            param,
            state.getExistingRealms(start),
            owner,
        )
    }
    editPopulation(call, state, town.population)
    editDataSources(state, town.sources)
}

// parse

fun parseTownId(parameters: Parameters, param: String) = TownId(parseInt(parameters, param))
fun parseOptionalTownId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { TownId(it) }

fun parseTown(parameters: Parameters, state: State, id: TownId): Town {
    val date = parseOptionalDate(parameters, state, DATE)

    return Town(
        id,
        parseName(parameters),
        parseOptionalNotEmptyString(parameters, TITLE),
        date,
        parseCreator(parameters),
        parseVitalStatus(parameters, state),
        parseHistory(parameters, OWNER, state, date) { _, _, param ->
            parseOptionalRealmId(parameters, param)
        },
        parsePopulation(parameters, state),
        parseDataSources(parameters),
    )
}
