package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.world.town.TownId
import kotlinx.serialization.Serializable

@Serializable
data class History(
    val owner: Owner = UnknownOwner,
    val previousOwners: List<HistoryEntry<Owner>> = emptyList(),
) {
    constructor(owner: Owner, previousOwner: HistoryEntry<Owner>) : this(owner, listOf(previousOwner))

    fun contains(character: CharacterId) = previousOwners.any { it.entry.contains(character) }

    fun contains(town: TownId) = previousOwners.any { it.entry.contains(town) }

}
