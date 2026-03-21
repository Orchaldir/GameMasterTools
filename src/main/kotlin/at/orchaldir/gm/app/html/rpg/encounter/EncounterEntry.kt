package at.orchaldir.gm.app.html.rpg.encounter

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseCharacterTemplateId
import at.orchaldir.gm.app.html.rpg.dice.editRandomNumber
import at.orchaldir.gm.app.html.rpg.dice.parseRandomNumber
import at.orchaldir.gm.app.html.util.editLookupTable
import at.orchaldir.gm.app.html.util.parseLookup
import at.orchaldir.gm.app.html.util.showLookupTable
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.IntRange
import at.orchaldir.gm.core.model.rpg.dice.ModifiedDiceRange
import at.orchaldir.gm.core.model.rpg.encounter.*
import at.orchaldir.gm.core.selector.util.sortCharacterTemplates
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEncounterEntryDetails(
    call: ApplicationCall,
    state: State,
    encounter: EncounterEntry,
) {
    when (encounter) {
        NoEncounter -> field("Encounter", "None")
        is CharacterTemplateEncounter -> field("Encounter") {
            showCharacterTemplateEncounter(call, state, encounter)
        }

        is CombinedEncounter -> fieldList("Encounter", encounter.list) { entry ->
            showEncounterEntryInternal(call, state, entry)
        }

        is EncounterTable -> showEncounterTable(encounter, call, state)
    }
}

private fun HtmlBlockTag.showEncounterEntryInternal(
    call: ApplicationCall,
    state: State,
    encounter: EncounterEntry,
) {
    when (encounter) {
        NoEncounter -> +"None"
        is CharacterTemplateEncounter -> showCharacterTemplateEncounter(call, state, encounter)
        is CombinedEncounter -> showList(encounter.list) { entry ->
            showEncounterEntryInternal(call, state, entry)
        }

        is EncounterTable -> showEncounterTable(encounter, call, state)
    }
}

private fun HtmlBlockTag.showCharacterTemplateEncounter(
    call: ApplicationCall,
    state: State,
    encounter: CharacterTemplateEncounter,
) {
    +encounter.amount.display()
    +" "
    link(call, state, encounter.template)
}

private fun HtmlBlockTag.showEncounterTable(
    encounter: EncounterTable,
    call: ApplicationCall,
    state: State,
) = showLookupTable(
    encounter.table,
    Pair("Encounter") { entry ->
        showEncounterEntryInternal(call, state, entry)
    },
)

// edit

fun HtmlBlockTag.editEncounterEntry(
    call: ApplicationCall,
    state: State,
    encounter: EncounterEntry,
    param: String,
) = showDetails("Encounter", true) {
    editEncounterEntryIntern(call, state, encounter, param)
}

fun HtmlBlockTag.editEncounterEntryIntern(
    call: ApplicationCall,
    state: State,
    encounter: EncounterEntry,
    param: String,
) {
    val range = ModifiedDiceRange(IntRange(0, 10), IntRange(0, 10))

    selectValue(
        "Type",
        combine(param, TYPE),
        EncounterEntryType.entries,
        encounter.getType(),
    )
    when (encounter) {
        NoEncounter -> doNothing()
        is CharacterTemplateEncounter -> {
            editRandomNumber(
                range,
                encounter.amount,
                combine(param, NUMBER),
                "Amount",
            )
            selectElement(
                state,
                combine(param, TEMPLATE),
                state.sortCharacterTemplates(),
                encounter.template,
            )
        }

        is CombinedEncounter -> editList(
            combine(param, LIST),
            encounter.list,
            2,
            100,
        ) { _, entryParam, entry ->
            editEncounterEntryIntern(call, state, entry, entryParam)
        }

        is EncounterTable -> editLookupTable(
            combine(param, LOOKUP),
            encounter.table,
            2,
            100,
            1,
            Pair("Encounter") { entryParam, entry ->
                editEncounterEntryIntern(call, state, entry, entryParam)
            },
        )
    }
}


// parse

fun parseEncounterEntry(
    parameters: Parameters,
    param: String,
): EncounterEntry = when (parse(parameters, combine(param, TYPE), EncounterEntryType.None)) {
    EncounterEntryType.None -> NoEncounter
    EncounterEntryType.CharacterTemplate -> CharacterTemplateEncounter(
        parseRandomNumber(parameters, combine(param, NUMBER)),
        parseCharacterTemplateId(parameters, combine(param, TEMPLATE)),
    )

    EncounterEntryType.Combined -> CombinedEncounter(
        parseList(parameters, combine(param, LIST), 2) { _, entryParam ->
            parseEncounterEntry(parameters, entryParam)
        }
    )

    EncounterEntryType.Table -> EncounterTable(
        parseLookup(parameters, combine(param, LOOKUP), 1) { entryParam ->
            parseEncounterEntry(parameters, entryParam)
        }
    )
}
