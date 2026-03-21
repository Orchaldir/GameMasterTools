package at.orchaldir.gm.app.html.rpg.encounter

import at.orchaldir.gm.app.LIST
import at.orchaldir.gm.app.LOOKUP
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.TEMPLATE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseCharacterTemplateId
import at.orchaldir.gm.app.html.rpg.dice.editRandomNumber
import at.orchaldir.gm.app.html.rpg.dice.parseRandomNumber
import at.orchaldir.gm.app.html.util.editLookupTable
import at.orchaldir.gm.app.html.util.parseLookup
import at.orchaldir.gm.app.html.util.showLookup
import at.orchaldir.gm.app.html.util.showLookupDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.IntRange
import at.orchaldir.gm.core.model.rpg.dice.ModifiedDiceRange
import at.orchaldir.gm.core.model.rpg.encounter.CharacterTemplateEncounter
import at.orchaldir.gm.core.model.rpg.encounter.CombinedEncounter
import at.orchaldir.gm.core.model.rpg.encounter.EncounterEntry
import at.orchaldir.gm.core.model.rpg.encounter.EncounterEntryType
import at.orchaldir.gm.core.model.rpg.encounter.EncounterTable
import at.orchaldir.gm.core.model.rpg.encounter.NoEncounter
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
        is EncounterTable -> showLookupDetails(encounter.table, "Encounter") { entry ->
            showEncounterEntryInternal(call, state, entry)
        }
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
        is CombinedEncounter -> showList( encounter.list) { entry ->
            showEncounterEntryDetails(call, state, entry)
        }
        is EncounterTable -> showLookup(encounter.table) { entry ->
            showEncounterEntryDetails(call, state, entry)
        }
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

// edit

fun HtmlBlockTag.editEncounterEntry(
    call: ApplicationCall,
    state: State,
    encounter: EncounterEntry,
    param: String,
) {
    val range = ModifiedDiceRange(IntRange(0, 10), IntRange(0, 10))
    showDetails("Encounter", true) {
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
                "Encounter",
                combine(param, LIST),
                encounter.list,
                2,
                100,
            ) { _, entryParam, entry ->
                editEncounterEntry(call, state, entry, entryParam)
            }
            is EncounterTable -> editLookupTable(
                combine(param, LOOKUP),
                encounter.table,
                2,
                100,
                listOf(
                    Pair("Encounter") { entryParam, entry ->
                        editEncounterEntry(call, state, entry, entryParam)
                    },
                ),
            )
        }
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
