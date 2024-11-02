package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.world.town.TownId
import kotlinx.serialization.Serializable

@Serializable
data class Ownership(
    val owner: Owner = UnknownOwner,
    val previousOwners: List<PreviousOwner> = emptyList(),
) {
    constructor(owner: Owner, previousOwner: PreviousOwner) : this(owner, listOf(previousOwner))

    fun contains(character: CharacterId) = previousOwners.any { it.owner.contains(character) }

    fun contains(town: TownId) = previousOwners.any { it.owner.contains(town) }

}
