package at.orchaldir.gm.app.html.gm.encounter

import at.orchaldir.gm.app.ENCOUNTER
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.util.showGenericUsage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.gm.encounter.Encounter
import at.orchaldir.gm.core.model.gm.encounter.EncounterId
import at.orchaldir.gm.core.selector.gm.encounter.getEncountersWith
import at.orchaldir.gm.core.selector.world.getRegionsWithEncounter
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEncounter(
    call: ApplicationCall,
    state: State,
    encounter: Encounter,
) {
    showEncounterEntry(call, state, encounter.entry)

    showUsage(call, state, encounter)
}

private fun HtmlBlockTag.showUsage(
    call: ApplicationCall,
    state: State,
    encounter: Encounter,
) = showGenericUsage(
    call,
    state,
    listOf(
        state.getEncountersWith(encounter.id),
        state.getRegionsWithEncounter(encounter.id),
    ),
)

// edit

fun HtmlBlockTag.editEncounter(
    call: ApplicationCall,
    state: State,
    encounter: Encounter,
) {
    selectName(encounter.name)
    editEncounterEntry(state, encounter.entry, ENCOUNTER, encounter.id)
}

// parse

fun parseEncounterId(parameters: Parameters, param: String) = EncounterId(parseInt(parameters, param))
fun parseEncounterId(value: String) = EncounterId(value.toInt())
fun parseOptionalEncounterId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { EncounterId(it) }

fun parseEncounter(
    state: State,
    parameters: Parameters,
    id: EncounterId,
) = Encounter(
    id,
    parseName(parameters),
    parseEncounterEntry(parameters, ENCOUNTER),
)
