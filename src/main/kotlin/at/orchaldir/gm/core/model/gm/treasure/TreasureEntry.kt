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
        is TreasureParcelLookup -> TreasureEntryType.Lookup
        is CombinedTreasure -> TreasureEntryType.Combined
        is TreasureTable -> TreasureEntryType.Table
    }

    fun <ID : Id<ID>> contains(id: ID): Boolean = when (this) {
        NoTreasure -> false
        is TreasureParcelLookup -> parcel == id
        is CombinedTreasure -> list.any { it.contains(id) }
        is TreasureTable -> table.entries.any { it.value.contains(id) }
    }

    fun validate(state: State, id: TreasureParcelId?): Unit = when (this) {
        NoTreasure -> doNothing()
        is TreasureParcelLookup -> {
            state.getTreasureParcelStorage().require(parcel)
            require(id != parcel) { "Cannot be based on itself!" }
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
data class TreasureParcelLookup(
    val parcel: TreasureParcelId,
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
