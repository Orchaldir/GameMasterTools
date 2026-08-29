package at.orchaldir.gm.core.model.gm.treasure

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.CurrencyUnitId
import at.orchaldir.gm.core.model.gm.treasure.EquipmentParcel
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelLookup
import at.orchaldir.gm.core.model.item.ammunition.AmmunitionId
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.util.quantity.Quantity
import at.orchaldir.gm.core.model.util.Lookup
import at.orchaldir.gm.core.model.util.quantity.FixedNumber
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TreasureEntryType {
    None,
    Ammunition,
    Combined,
    Equipment,
    Lookup,
    Money,
    Table,
    Text,
}

@Serializable
sealed class TreasureEntry {

    fun getType() = when (this) {
        NoTreasure -> TreasureEntryType.None
        is AmmunitionParcel -> TreasureEntryType.Ammunition
        is CombinedTreasure -> TreasureEntryType.Combined
        is EquipmentParcel -> TreasureEntryType.Equipment
        is MoneyParcel -> TreasureEntryType.Money
        is TextParcel -> TreasureEntryType.Text
        is TreasureParcelLookup -> TreasureEntryType.Lookup
        is TreasureTable -> TreasureEntryType.Table
    }

    fun <ID : Id<ID>> contains(id: ID): Boolean = when (this) {
        NoTreasure -> false
        is AmmunitionParcel -> map.containsKey<Id<*>>(id)
        is CombinedTreasure -> list.any { it.contains(id) }
        is EquipmentParcel -> map.containsKey<Id<*>>(id)
        is MoneyParcel -> map.containsKey<Id<*>>(id)
        is TextParcel -> map.containsKey<Id<*>>(id)
        is TreasureParcelLookup -> map.containsKey<Id<*>>(id)
        is TreasureTable -> table.entries.any { it.value.contains(id) }
    }

    fun validate(state: State, id: TreasureParcelId?): Unit = when (this) {
        NoTreasure -> doNothing()
        is AmmunitionParcel -> state.getAmmunitionStorage().require(map.keys)
        is CombinedTreasure -> list.forEach { it.validate(state, id) }
        is EquipmentParcel -> state.getEquipmentStorage().require(map.keys)
        is MoneyParcel -> state.getCurrencyUnitStorage().require(map.keys)
        is TextParcel -> state.getTextStorage().require(map.keys)

        is TreasureParcelLookup -> {
            state.getTreasureParcelStorage().require(map.keys)
            require(!map.contains(id)) { "Cannot be based on itself!" }
        }
        is TreasureTable -> table.entries.forEach { it.value.validate(state, id) }
    }
}

@Serializable
@SerialName("None")
data object NoTreasure : TreasureEntry()

@Serializable
@SerialName("Ammunition")
data class AmmunitionParcel(
    val map: Map<AmmunitionId, Quantity>,
) : TreasureEntry() {
    constructor(id: AmmunitionId): this(mapOf(id to FixedNumber(1)))
}

@Serializable
@SerialName("Combined")
data class CombinedTreasure(
    val list: List<TreasureEntry>,
) : TreasureEntry()

@Serializable
@SerialName("Equipment")
data class EquipmentParcel(
    val map: Map<EquipmentId, Quantity>,
) : TreasureEntry() {
    constructor(id: EquipmentId): this(mapOf(id to FixedNumber(1)))
}

@Serializable
@SerialName("Money")
data class MoneyParcel(
    val map: Map<CurrencyUnitId, Quantity>,
) : TreasureEntry()

@Serializable
@SerialName("Text")
data class TextParcel(
    val map: Map<TextId, Quantity>,
) : TreasureEntry() {
    constructor(id: TextId): this(mapOf(id to FixedNumber(1)))
}

@Serializable
@SerialName("Lookup")
data class TreasureParcelLookup(
    val map: Map<TreasureParcelId, Quantity>,
) : TreasureEntry() {
    constructor(id: TreasureParcelId): this(mapOf(id to FixedNumber(1)))
}

@Serializable
@SerialName("Table")
data class TreasureTable(
    val table: Lookup<TreasureEntry>,
) : TreasureEntry()
