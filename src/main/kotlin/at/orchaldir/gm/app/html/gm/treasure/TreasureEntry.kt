package at.orchaldir.gm.app.html.gm.treasure

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.editLookupTable
import at.orchaldir.gm.app.html.util.parseLookup
import at.orchaldir.gm.app.html.util.showLookupTable
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.gm.treasure.CombinedTreasure
import at.orchaldir.gm.core.model.gm.treasure.NoTreasure
import at.orchaldir.gm.core.model.gm.treasure.TreasureEntry
import at.orchaldir.gm.core.model.gm.treasure.TreasureEntryType
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelId
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelLookup
import at.orchaldir.gm.core.model.gm.treasure.TreasureTable
import at.orchaldir.gm.core.model.rpg.dice.ModifiedDiceRange
import at.orchaldir.gm.core.selector.util.sortCharacterTemplates
import at.orchaldir.gm.core.selector.util.sortTreasureParcels
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.RangeInt
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTreasureEntry(
    call: ApplicationCall,
    state: State,
    entry: TreasureEntry,
) {
    when (entry) {
        NoTreasure -> field("Treasure", "None")
        is TreasureParcelLookup -> fieldLink("Treasure", call, state, entry.parcel)
        is CombinedTreasure -> fieldList("Treasure", entry.list) { entry ->
            showTreasureEntryInternal(call, state, entry)
        }

        is TreasureTable -> showTreasureTable(entry, call, state)
    }
}

private fun HtmlBlockTag.showTreasureEntryInternal(
    call: ApplicationCall,
    state: State,
    entry: TreasureEntry,
) {
    when (entry) {
        NoTreasure -> +"None"
        is TreasureParcelLookup -> link(call, state, entry.parcel)
        is CombinedTreasure -> showList(entry.list) { entry ->
            showTreasureEntryInternal(call, state, entry)
        }

        is TreasureTable -> showTreasureTable(entry, call, state)
    }
}

private fun HtmlBlockTag.showTreasureTable(
    entry: TreasureTable,
    call: ApplicationCall,
    state: State,
) = showLookupTable(
    entry.table,
    Pair("Treasure") { entry ->
        showTreasureEntryInternal(call, state, entry)
    },
)

// edit

fun HtmlBlockTag.editTreasureEntry(
    state: State,
    entry: TreasureEntry,
    param: String,
    id: TreasureParcelId?,
) = showDetails("Treasure", true) {
    editTreasureEntryIntern(state, entry, param, id)
}

fun HtmlBlockTag.editTreasureEntryIntern(
    state: State,
    entry: TreasureEntry,
    param: String,
    id: TreasureParcelId?,
) {
    val range = ModifiedDiceRange(RangeInt(0, 10), RangeInt(0, 10))
    val entries = state.sortTreasureParcels()
        .filter { it.id != id }
    val templates = state.sortCharacterTemplates()
    val allEmpty = entries.isEmpty() && templates.isEmpty()

    selectValue(
        "Type",
        combine(param, TYPE),
        TreasureEntryType.entries,
        entry.getType(),
    ) {
        when (it) {
            TreasureEntryType.None -> false
            TreasureEntryType.Lookup -> entries.isEmpty()
            TreasureEntryType.Combined -> allEmpty
            TreasureEntryType.Table -> allEmpty
        }
    }

    when (entry) {
        NoTreasure -> doNothing()
        is TreasureParcelLookup -> {
            selectElement(
                state,
                combine(param, ENCOUNTER),
                entries,
                entry.parcel,
            )
        }

        is CombinedTreasure -> editList(
            combine(param, LIST),
            entry.list,
            2,
            100,
        ) { _, entryParam, entry ->
            editTreasureEntryIntern(state, entry, entryParam, id)
        }

        is TreasureTable -> editLookupTable(
            combine(param, LOOKUP),
            entry.table,
            2,
            100,
            1,
            Pair("Treasure") { entryParam, entry ->
                editTreasureEntryIntern(state, entry, entryParam, id)
            },
        )
    }
}


// parse

fun parseTreasureEntry(
    parameters: Parameters,
    param: String,
): TreasureEntry = when (parse(parameters, combine(param, TYPE), TreasureEntryType.None)) {
    TreasureEntryType.None -> NoTreasure
    TreasureEntryType.Lookup -> TreasureParcelLookup(
        parseTreasureParcelId(parameters, combine(param, ENCOUNTER)),
    )

    TreasureEntryType.Combined -> CombinedTreasure(
        parseList(parameters, combine(param, LIST), 2) { _, entryParam ->
            parseTreasureEntry(parameters, entryParam)
        }
    )

    TreasureEntryType.Table -> TreasureTable(
        parseLookup(parameters, combine(param, LOOKUP), 1) { entryParam ->
            parseTreasureEntry(parameters, entryParam)
        }
    )
}
