package at.orchaldir.gm.app.html.gm.treasure

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.parseOptionalCurrencyUnitId
import at.orchaldir.gm.app.html.rpg.dice.editRandomNumber
import at.orchaldir.gm.app.html.rpg.dice.parseRandomNumber
import at.orchaldir.gm.app.html.util.editLookupTable
import at.orchaldir.gm.app.html.util.parseLookup
import at.orchaldir.gm.app.html.util.showLookupTable
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyUnitId
import at.orchaldir.gm.core.model.gm.treasure.CombinedTreasure
import at.orchaldir.gm.core.model.gm.treasure.MoneyParcel
import at.orchaldir.gm.core.model.gm.treasure.NoTreasure
import at.orchaldir.gm.core.model.gm.treasure.TreasureEntry
import at.orchaldir.gm.core.model.gm.treasure.TreasureEntryType
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelId
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelLookup
import at.orchaldir.gm.core.model.gm.treasure.TreasureTable
import at.orchaldir.gm.core.model.rpg.dice.ModifiedDiceRange
import at.orchaldir.gm.core.model.rpg.dice.RandomNumber
import at.orchaldir.gm.core.model.util.SortCurrencyUnit
import at.orchaldir.gm.core.selector.util.sortCurrencyUnits
import at.orchaldir.gm.core.selector.util.sortTreasureParcels
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
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
        is TreasureTable -> showTreasureTable(call, state, entry)
        else ->  field("Treasure",) {
            showTreasureEntryInternal(call, state, entry)
        }
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

        is MoneyParcel -> showTreasureMap(call, state, state.getCurrencyUnitStorage(), entry.currencyUnits)
        is TreasureParcelLookup -> showTreasureMap(call, state, state.getTreasureParcelStorage(), entry.lookup)
        is TreasureTable -> showTreasureTable(call, state, entry)
    }
}

private fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.showTreasureMap(
    call: ApplicationCall,
    state: State,
    storage: Storage<ID, ELEMENT>,
    map: Map<ID, RandomNumber>,
) {
    val units = map.mapKeys {
        storage.getOrThrow(it.key)
    }

    showInlineList(units.entries) { (unit, number) ->
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
    allowedTypes: Collection<TreasureEntryType> = TreasureEntryType.entries,
) {
    val range = ModifiedDiceRange(RangeInt(0, 10), RangeInt(0, 10))
    val parcels = state.sortTreasureParcels()
        .filter { it.id != id }
    var currencyUnits = state.sortCurrencyUnits(SortCurrencyUnit.Value)
    val allEmpty = parcels.isEmpty() && currencyUnits.isEmpty()

    selectValue(
        "Type",
        combine(param, TYPE),
        allowedTypes,
        entry.getType(),
    ) {
        when (it) {
            TreasureEntryType.None -> false
            TreasureEntryType.Lookup -> parcels.isEmpty()
            TreasureEntryType.Combined -> allEmpty
            TreasureEntryType.Money -> currencyUnits.isEmpty()
            TreasureEntryType.Table -> allEmpty
        }
    }

    when (entry) {
        NoTreasure -> doNothing()

        is CombinedTreasure -> {
            val allowed = (allowedTypes - TreasureEntryType.Combined - TreasureEntryType.Table - TreasureEntryType.None)
                .toMutableList()
            val maxSize = if (parcels.isEmpty()) {
                allowed -= TreasureEntryType.Lookup
                allowed.size
            } else {
                allowed.size + parcels.size - 1
            }

            editList(
                combine(param, LIST),
                entry.list,
                2,
                maxSize,
            ) { _, combinedParam, combinedEntry ->
                editTreasureEntryIntern(state, combinedEntry, combinedParam, id, allowed)

                if (combinedEntry.getType() != TreasureEntryType.Lookup) {
                    allowed -= combinedEntry.getType()
                }
            }
        }

        is MoneyParcel -> editTreasureMap(
            state,
            currencyUnits,
            range,
            combine(param, CURRENCY),
            entry.currencyUnits,
            "Money",
        )

        is TreasureParcelLookup -> editTreasureMap(
            state,
            parcels,
            range,
            combine(param, LOOKUP),
            entry.lookup,
            "Lookup",
        )

        is TreasureTable -> editLookupTable(
            combine(param, RANDOM),
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

private fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.editTreasureMap(
    state: State,
    elements: List<ELEMENT>,
    range: ModifiedDiceRange,
    param: String,
    map: Map<ID, RandomNumber>,
    text: String,
) {
    val remaining = elements.toMutableList()

    editMap(
        text,
        param,
        map,
        1,
        remaining.size,
    ) { _, entryParam, entryId, amount ->
        selectElement(
            state,
            combine(entryParam, TYPE),
            remaining,
            entryId,
        )
        editRandomNumber(
            range,
            amount,
            combine(entryParam, NUMBER),
            "Amount",
        )

        remaining.removeIf { it.id() != entryId }
    }
}


// parse

fun parseTreasureEntry(
    state: State,
    parameters: Parameters,
    param: String,
    id: TreasureParcelId?,
    allowedTypes: Collection<TreasureEntryType> = TreasureEntryType.entries,
): TreasureEntry = when (parse(parameters, combine(param, TYPE), allowedTypes)) {
    TreasureEntryType.None -> NoTreasure

    TreasureEntryType.Combined -> {
        // extract
        val allowed = (allowedTypes - TreasureEntryType.Combined - TreasureEntryType.Table - TreasureEntryType.None)
            .toMutableList()
        if (state.getTreasureParcelStorage().isEmptyWithout(id)) {
            allowed -= TreasureEntryType.Lookup
        }

        CombinedTreasure(
            // handle allowed
            parseList(parameters, combine(param, LIST), 2) { _, entryParam ->
                val entry = parseTreasureEntry(state, parameters, entryParam, id, allowed)

                // count lookups or limit to 1 lookup?
                if (entry.getType() != TreasureEntryType.Lookup) {
                    allowed -= entry.getType()
                }

                entry
            }
        )
    }

    TreasureEntryType.Lookup -> TreasureParcelLookup(
        parseTreasureMap(
            parameters,
            state.getTreasureParcelStorage(),
            combine(param, LOOKUP),
            ::parseTreasureParcelId,
        ),
    )

    TreasureEntryType.Money -> MoneyParcel(
        parseTreasureMap(
            parameters,
            state.getCurrencyUnitStorage(),
            combine(param, CURRENCY),
            ::parseOptionalCurrencyUnitId,
        ),
    )

    TreasureEntryType.Table -> TreasureTable(
        parseLookup(parameters, combine(param, RANDOM), 1, 2) { entryParam ->
            parseTreasureEntry(state, parameters, entryParam, id)
        }
    )
}

private fun <ID : Id<ID>, ELEMENT : Element<ID>> parseTreasureMap(
    parameters: Parameters,
    storage: Storage<ID, ELEMENT>,
    param: String,
    parseId: (Parameters, String) -> ID?,
): Map<ID, RandomNumber> = parseMap(
    parameters,
    param,
    storage.getIds(),
    { _, keyParam -> parseId(parameters, combine(keyParam, TYPE)) },
    { _, _, valueParam -> parseRandomNumber(parameters, combine(valueParam, NUMBER)) },
    1,
)
