package at.orchaldir.gm.app.html.gm.treasure

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.parseOptionalCurrencyUnitId
import at.orchaldir.gm.app.html.item.ammunition.parseOptionalAmmunitionId
import at.orchaldir.gm.app.html.item.equipment.parseOptionalEquipmentId
import at.orchaldir.gm.app.html.item.text.parseOptionalTextId
import at.orchaldir.gm.app.html.rpg.dice.editRandomNumber
import at.orchaldir.gm.app.html.rpg.dice.parseRandomNumber
import at.orchaldir.gm.app.html.util.editLookupTable
import at.orchaldir.gm.app.html.util.parseLookup
import at.orchaldir.gm.app.html.util.showLookupTable
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyUnit
import at.orchaldir.gm.core.model.gm.treasure.AmmunitionParcel
import at.orchaldir.gm.core.model.gm.treasure.CombinedTreasure
import at.orchaldir.gm.core.model.gm.treasure.EquipmentParcel
import at.orchaldir.gm.core.model.gm.treasure.MoneyParcel
import at.orchaldir.gm.core.model.gm.treasure.NoTreasure
import at.orchaldir.gm.core.model.gm.treasure.TextParcel
import at.orchaldir.gm.core.model.gm.treasure.TreasureEntry
import at.orchaldir.gm.core.model.gm.treasure.TreasureEntryType
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcel
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelId
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelLookup
import at.orchaldir.gm.core.model.gm.treasure.TreasureTable
import at.orchaldir.gm.core.model.rpg.dice.ModifiedDiceRange
import at.orchaldir.gm.core.model.rpg.dice.RandomNumber
import at.orchaldir.gm.core.model.util.SortCurrencyUnit
import at.orchaldir.gm.core.selector.util.sortAmmunition
import at.orchaldir.gm.core.selector.util.sortCurrencyUnits
import at.orchaldir.gm.core.selector.util.sortEquipmentList
import at.orchaldir.gm.core.selector.util.sortTexts
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
        is AmmunitionParcel -> showTreasureMap(call, state, state.getAmmunitionStorage(), entry.map)
        is CombinedTreasure -> showList(entry.list) { entry ->
            showTreasureEntryInternal(call, state, entry)
        }

        is EquipmentParcel -> showTreasureMap(call, state, state.getEquipmentStorage(), entry.map)
        is MoneyParcel -> showTreasureMap(call, state, state.getCurrencyUnitStorage(), entry.map)
        is TextParcel -> showTreasureMap(call, state, state.getTextStorage(), entry.map)
        is TreasureParcelLookup -> showTreasureMap(call, state, state.getTreasureParcelStorage(), entry.map)
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
    val ammunitionList = state.sortAmmunition()
    val moneyList = state.sortCurrencyUnits(SortCurrencyUnit.Value)
    val equipmentList = state.sortEquipmentList()
    val texts = state.sortTexts()
    val parcels = state.sortTreasureParcels()
        .filter { it.id != id }
    val allEmpty = ammunitionList.isEmpty() && equipmentList.isEmpty() && moneyList.isEmpty() && parcels.isEmpty() && texts.isEmpty()

    selectValue(
        "Type",
        combine(param, TYPE),
        allowedTypes,
        entry.getType(),
    ) {
        when (it) {
            TreasureEntryType.None -> false
            TreasureEntryType.Ammunition -> ammunitionList.isEmpty()
            TreasureEntryType.Combined -> allEmpty
            TreasureEntryType.Equipment -> equipmentList.isEmpty()
            TreasureEntryType.Lookup -> parcels.isEmpty()
            TreasureEntryType.Money -> moneyList.isEmpty()
            TreasureEntryType.Text -> texts.isEmpty()
            TreasureEntryType.Table -> allEmpty
        }
    }

    when (entry) {
        NoTreasure -> doNothing()
        is AmmunitionParcel -> editTreasureMap(
            state,
            ammunitionList,
            range,
            combine(param, AMMUNITION),
            entry.map,
            "Ammunition",
        )

        is CombinedTreasure -> {
            val allowed = caalculatedAllowedForCombined(allowedTypes, moneyList, parcels)

            editList(
                combine(param, LIST),
                entry.list,
                2,
                100,
            ) { _, combinedParam, combinedEntry ->
                editTreasureEntryIntern(state, combinedEntry, combinedParam, id, allowed)

                if (combinedEntry.getType() != TreasureEntryType.Table) {
                    allowed -= combinedEntry.getType()
                }
            }
        }

        is EquipmentParcel -> editTreasureMap(
            state,
            equipmentList,
            range,
            combine(param, EQUIPMENT),
            entry.map,
            "Equipment",
        )

        is MoneyParcel -> editTreasureMap(
            state,
            moneyList,
            range,
            combine(param, CURRENCY),
            entry.map,
            "Money",
        )

        is TextParcel -> editTreasureMap(
            state,
            texts,
            range,
            combine(param, TEXT),
            entry.map,
            "Texts",
        )

        is TreasureParcelLookup -> editTreasureMap(
            state,
            parcels,
            range,
            combine(param, LOOKUP),
            entry.map,
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

private fun caalculatedAllowedForCombined(
    allowedTypes: Collection<TreasureEntryType>,
    currencyUnits: Collection<CurrencyUnit>,
    parcels: Collection<TreasureParcel>,
): MutableList<TreasureEntryType> {
    val allowed = (allowedTypes - TreasureEntryType.Combined - TreasureEntryType.None)
        .toMutableList()
    if (currencyUnits.isEmpty()) {
        allowed -= TreasureEntryType.Money
    }
    if (parcels.isEmpty()) {
        allowed -= TreasureEntryType.Lookup
    }

    return allowed
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

        remaining.removeIf { it.id() == entryId }
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

    TreasureEntryType.Ammunition -> AmmunitionParcel(
        parseTreasureMap(
            parameters,
            state.getAmmunitionStorage(),
            combine(param, AMMUNITION),
            ::parseOptionalAmmunitionId,
        ),
    )

    TreasureEntryType.Combined -> {
        val allowed = caalculatedAllowedForCombined(
            allowedTypes,
            state.getCurrencyUnitStorage().getAll(),
            state.getTreasureParcelStorage().getAllExcept(id),
        )

        CombinedTreasure(
            parseList(parameters, combine(param, LIST), 2) { _, entryParam ->
                val entry = parseTreasureEntry(state, parameters, entryParam, id, allowed)

                if (entry.getType() != TreasureEntryType.Table) {
                    allowed -= entry.getType()
                }

                entry
            }
        )
    }

    TreasureEntryType.Money -> MoneyParcel(
        parseTreasureMap(
            parameters,
            state.getCurrencyUnitStorage(),
            combine(param, CURRENCY),
            ::parseOptionalCurrencyUnitId,
        ),
    )

    TreasureEntryType.Lookup -> TreasureParcelLookup(
        parseTreasureMap(
            parameters,
            state.getTreasureParcelStorage(),
            combine(param, LOOKUP),
            ::parseTreasureParcelId,
        ),
    )

    TreasureEntryType.Equipment -> EquipmentParcel(
        parseTreasureMap(
            parameters,
            state.getEquipmentStorage(),
            combine(param, EQUIPMENT),
            ::parseOptionalEquipmentId,
        ),
    )

    TreasureEntryType.Table -> TreasureTable(
        parseLookup(parameters, combine(param, RANDOM), 1, 2) { entryParam ->
            parseTreasureEntry(state, parameters, entryParam, id)
        }
    )

    TreasureEntryType.Text -> TextParcel(
        parseTreasureMap(
            parameters,
            state.getTextStorage(),
            combine(param, TEXT),
            ::parseOptionalTextId,
        ),
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
