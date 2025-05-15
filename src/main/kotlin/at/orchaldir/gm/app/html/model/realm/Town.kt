package at.orchaldir.gm.app.html.model.town

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.MAP
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.world.parseOptionalTownMapId
import at.orchaldir.gm.app.html.optionalFieldLink
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.realm.TownId
import at.orchaldir.gm.core.model.world.town.TownMapId
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
