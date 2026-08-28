package at.orchaldir.gm.core.model.gm.treasure

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyUnitId
import at.orchaldir.gm.core.model.rpg.dice.RandomNumber
import at.orchaldir.gm.core.model.util.Lookup
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TreasureEntryType {
    None,
    Lookup,
    Combined,
    Money,
    Table,
}

@Serializable
sealed class TreasureEntry {

    fun getType() = when (this) {
        NoTreasure -> TreasureEntryType.None
        is CombinedTreasure -> TreasureEntryType.Combined
        is MoneyParcel -> TreasureEntryType.Money
        is TreasureParcelLookup -> TreasureEntryType.Lookup
        is TreasureTable -> TreasureEntryType.Table
    }

    fun <ID : Id<ID>> contains(id: ID): Boolean = when (this) {
        NoTreasure -> false
        is CombinedTreasure -> list.any { it.contains(id) }
        is MoneyParcel -> currencyUnits.containsKey<Id<*>>(id)
        is TreasureParcelLookup -> lookup.containsKey<Id<*>>(id)
        is TreasureTable -> table.entries.any { it.value.contains(id) }
    }

    fun validate(state: State, id: TreasureParcelId?): Unit = when (this) {
        NoTreasure -> doNothing()
        is CombinedTreasure -> list.forEach { it.validate(state, id) }
        is MoneyParcel -> state.getCurrencyUnitStorage().require(currencyUnits.keys)

        is TreasureParcelLookup -> {
            state.getTreasureParcelStorage().require(lookup.keys)
            require(!lookup.contains(id)) { "Cannot be based on itself!" }
        }
        is TreasureTable -> table.entries.forEach { it.value.validate(state, id) }
    }
}

@Serializable
@SerialName("None")
data object NoTreasure : TreasureEntry()

@Serializable
@SerialName("Combined")
data class CombinedTreasure(
    val list: List<TreasureEntry>,
) : TreasureEntry()

@Serializable
@SerialName("Money")
data class MoneyParcel(
    val currencyUnits: Map<CurrencyUnitId, RandomNumber>,
) : TreasureEntry()

@Serializable
@SerialName("Lookup")
data class TreasureParcelLookup(
    val lookup: Map<TreasureParcelId, RandomNumber>,
) : TreasureEntry()

@Serializable
@SerialName("Table")
data class TreasureTable(
    val table: Lookup<TreasureEntry>,
) : TreasureEntry()
