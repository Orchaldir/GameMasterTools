package at.orchaldir.gm.core.model.rpg.loot

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Lookup
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class LootEntryType {
    None,
    Lookup,
    Combined,
    Table,
}

@Serializable
sealed class LootEntry {

    fun getType() = when (this) {
        NoLoot -> LootEntryType.None
        is LootLookup -> LootEntryType.Lookup
        is CombinedLoot -> LootEntryType.Combined
        is LootTable -> LootEntryType.Table
    }

    fun <ID : Id<ID>> contains(id: ID): Boolean = when (this) {
        NoLoot -> false
        is LootLookup -> loot == id
        is CombinedLoot -> list.any { it.contains(id) }
        is LootTable -> table.entries.any { it.value.contains(id) }
    }

    fun validate(state: State, id: LootId?): Unit = when (this) {
        NoLoot -> doNothing()
        is LootLookup -> {
            state.getLootStorage().require(loot)
            require(id != loot) { "Cannot be based on itself!" }
        }

        is CombinedLoot -> list.forEach { it.validate(state, id) }
        is LootTable -> table.entries.forEach { it.value.validate(state, id) }
    }
}

@Serializable
@SerialName("None")
data object NoLoot : LootEntry()

@Serializable
@SerialName("Lookup")
data class LootLookup(
    val loot: LootId,
) : LootEntry()

@Serializable
@SerialName("Combined")
data class CombinedLoot(
    val list: List<LootEntry>,
) : LootEntry()

@Serializable
@SerialName("Table")
data class LootTable(
    val table: Lookup<LootEntry>,
) : LootEntry()
