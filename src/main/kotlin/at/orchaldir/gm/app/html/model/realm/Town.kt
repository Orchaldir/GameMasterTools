package at.orchaldir.gm.app.html.model.town

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.world.showBuildingsOfTownMap
import at.orchaldir.gm.app.html.model.world.showCharactersOfTownMap
import at.orchaldir.gm.app.html.optionalFieldLink
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.selector.util.sortTownMaps
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
    fieldCreator(call, state, town.founder, "Founder")
    optionalField(call, state, "Date", town.foundingDate)

    val currentOptionalTownMaps = state.getCurrentTownMap(town.id)

    currentOptionalTownMaps?.let { currentTownMap ->
        optionalFieldLink(call, state, currentTownMap.id)
        val previousTownMaps = state.sortTownMaps(state.getTownMaps(town.id) - currentTownMap)
        fieldList(call, state, "Previous Town Maps", previousTownMaps)

        showBuildingsOfTownMap(call, state, currentTownMap.id)
    }
    showCharactersOfTownMap(call, state, town.id, currentOptionalTownMaps?.id)

    showCreated(call, state, town.id)
    showOwnedElements(call, state, town.id)
    showDataSources(call, state, town.sources)
}

// edit

fun FORM.editTown(
    state: State,
    town: Town,
) {
    selectName(town.name)
    selectOptionalDate(state, "Date", town.foundingDate, DATE)
    selectCreator(state, town.founder, town.id, town.foundingDate, "Founder")
    editDataSources(state, town.sources)
}

// parse

fun parseTownId(parameters: Parameters, param: String) = TownId(parseInt(parameters, param))
fun parseOptionalTownId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { TownId(it) }

fun parseTown(parameters: Parameters, state: State, id: TownId) = Town(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, DATE),
    parseCreator(parameters),
    parseDataSources(parameters),
)
