package at.orchaldir.gm.core.model.gm.treasure

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Lookup
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TreasureEntryType {
    None,
    Lookup,
    Combined,
    Table,
}

@Serializable
sealed class TreasureEntry {

    fun getType() = when (this) {
        NoTreasure -> TreasureEntryType.None
        is TreasureLookup -> TreasureEntryType.Lookup
        is CombinedTreasure -> TreasureEntryType.Combined
        is TreasureTable -> TreasureEntryType.Table
    }

    fun <ID : Id<ID>> contains(id: ID): Boolean = when (this) {
        NoTreasure -> false
        is TreasureLookup -> loot == id
        is CombinedTreasure -> list.any { it.contains(id) }
        is TreasureTable -> table.entries.any { it.value.contains(id) }
    }

    fun validate(state: State, id: TreasureParcelId?): Unit = when (this) {
        NoTreasure -> doNothing()
        is TreasureLookup -> {
            state.getTreasureParcelStorage().require(loot)
            require(id != loot) { "Cannot be based on itself!" }
        }

        is CombinedTreasure -> list.forEach { it.validate(state, id) }
        is TreasureTable -> table.entries.forEach { it.value.validate(state, id) }
    }
}

@Serializable
@SerialName("None")
data object NoTreasure : TreasureEntry()

@Serializable
@SerialName("Lookup")
data class TreasureLookup(
    val loot: TreasureParcelId,
) : TreasureEntry()

@Serializable
@SerialName("Combined")
data class CombinedTreasure(
    val list: List<TreasureEntry>,
) : TreasureEntry()

@Serializable
@SerialName("Table")
data class TreasureTable(
    val table: Lookup<TreasureEntry>,
) : TreasureEntry()
