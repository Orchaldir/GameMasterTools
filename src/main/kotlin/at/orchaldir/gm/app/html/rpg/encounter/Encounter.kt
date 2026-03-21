package at.orchaldir.gm.app.html.rpg.encounter

import at.orchaldir.gm.app.ENCOUNTER
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.encounter.Encounter
import at.orchaldir.gm.core.model.rpg.encounter.EncounterId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEncounter(
    call: ApplicationCall,
    state: State,
    encounter: Encounter,
) {
    showEncounterEntryDetails(call, state, encounter.entry)
}

// edit

fun HtmlBlockTag.editEncounter(
    call: ApplicationCall,
    state: State,
    encounter: Encounter,
) {
    selectName(encounter.name)
    editEncounterEntry(call, state, encounter.entry, ENCOUNTER)
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
