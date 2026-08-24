package at.orchaldir.gm.app.html.gm.treasure

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.parseCurrencyUnitId
import at.orchaldir.gm.app.html.economy.money.parseOptionalCurrencyUnitId
import at.orchaldir.gm.app.html.rpg.dice.editRandomNumber
import at.orchaldir.gm.app.html.rpg.dice.parseRandomNumber
import at.orchaldir.gm.app.html.util.editLookupTable
import at.orchaldir.gm.app.html.util.parseLookup
import at.orchaldir.gm.app.html.util.showLookupTable
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.gm.treasure.CombinedTreasure
import at.orchaldir.gm.core.model.gm.treasure.MoneyParcel
import at.orchaldir.gm.core.model.gm.treasure.NoTreasure
import at.orchaldir.gm.core.model.gm.treasure.TreasureEntry
import at.orchaldir.gm.core.model.gm.treasure.TreasureEntryType
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelId
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelLookup
import at.orchaldir.gm.core.model.gm.treasure.TreasureTable
import at.orchaldir.gm.core.model.rpg.dice.ModifiedDiceRange
import at.orchaldir.gm.core.model.util.SortCurrencyUnit
import at.orchaldir.gm.core.selector.util.sortCurrencyUnits
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
        is CombinedTreasure -> fieldList("Treasure", entry.list) { entry ->
            showTreasureEntryInternal(call, state, entry)
        }
        is MoneyParcel -> field("Treasure",) {
            showMoneyParcel(call, state, entry)
        }
        is TreasureParcelLookup -> fieldLink("Treasure", call, state, entry.parcel)
        is TreasureTable -> showTreasureTable(call, state, entry)
    }
}

private fun HtmlBlockTag.showTreasureEntryInternal(
    call: ApplicationCall,
    state: State,
    entry: TreasureEntry,
) {
    when (entry) {
        NoTreasure -> +"None"
        is CombinedTreasure -> showList(entry.list) { entry ->
            showTreasureEntryInternal(call, state, entry)
        }

        is MoneyParcel -> showMoneyParcel(call, state, entry)
        is TreasureParcelLookup -> link(call, state, entry.parcel)
        is TreasureTable -> showTreasureTable(call, state, entry)
    }
}

private fun HtmlBlockTag.showMoneyParcel(
    call: ApplicationCall,
    state: State,
    entry: MoneyParcel,
) {
    val storage = state.getCurrencyUnitStorage()
    val units = entry.currencyUnits.mapKeys {
        storage.getOrThrow(it.key)
    }

    showList(units.entries) { (unit, number) ->
        +number.display()
        +" "
        link(call, state, unit)
    }
}

private fun HtmlBlockTag.showTreasureTable(
    call: ApplicationCall,
    state: State,
    entry: TreasureTable,
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
    var currencyUnits = state.sortCurrencyUnits(SortCurrencyUnit.Value)
    val allEmpty = entries.isEmpty() && currencyUnits.isEmpty()

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
            TreasureEntryType.Money -> currencyUnits.isEmpty()
            TreasureEntryType.Table -> allEmpty
        }
    }

    when (entry) {
        NoTreasure -> doNothing()

        is CombinedTreasure -> editList(
            combine(param, LIST),
            entry.list,
            2,
            100,
        ) { _, entryParam, entry ->
            editTreasureEntryIntern(state, entry, entryParam, id)
        }

        is MoneyParcel -> editMap(
            "Currency Units",
            combine(param, CURRENCY),
            entry.currencyUnits,
            1,
            currencyUnits.size,
        ) { _, entryParam, currencyUnitId, amount ->
            selectElement(
                state,
                combine(entryParam, TYPE),
                currencyUnits,
                currencyUnitId,
            )
            editRandomNumber(
                range,
                amount,
                combine(entryParam, NUMBER),
                "Amount",
            )

            currencyUnits = currencyUnits.filter { it.id != currencyUnitId }
        }

        is TreasureParcelLookup -> {
            selectElement(
                state,
                combine(param, ENCOUNTER),
                entries,
                entry.parcel,
            )
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
    state: State,
    parameters: Parameters,
    param: String,
): TreasureEntry = when (parse(parameters, combine(param, TYPE), TreasureEntryType.None)) {
    TreasureEntryType.None -> NoTreasure
    TreasureEntryType.Lookup -> TreasureParcelLookup(
        parseTreasureParcelId(parameters, combine(param, ENCOUNTER)),
    )

    TreasureEntryType.Combined -> CombinedTreasure(
        parseList(parameters, combine(param, LIST), 2) { _, entryParam ->
            parseTreasureEntry(state, parameters, entryParam)
        }
    )

    TreasureEntryType.Money -> MoneyParcel(
        parseMap(
            parameters,
            combine(param, CURRENCY),
            state.getCurrencyUnitStorage().getIds(),
            { _, keyParam -> parseOptionalCurrencyUnitId(parameters, combine(keyParam, TYPE)) },
            { _, _, valueParam -> parseRandomNumber(parameters, combine(valueParam, NUMBER)) },
        )
    )

    TreasureEntryType.Table -> TreasureTable(
        parseLookup(parameters, combine(param, LOOKUP), 1) { entryParam ->
            parseTreasureEntry(state, parameters, entryParam)
        }
    )
}
