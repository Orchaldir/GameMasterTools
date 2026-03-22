package at.orchaldir.gm.app.html.rpg.encounter

import at.orchaldir.gm.app.ENCOUNTER
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.encounter.Encounter
import at.orchaldir.gm.core.model.rpg.encounter.EncounterId
import at.orchaldir.gm.core.selector.rpg.encounter.getEncountersWith
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

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
) {
    val encounters = state.getEncountersWith(encounter.id)

    if (encounters.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, encounters)
}

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
