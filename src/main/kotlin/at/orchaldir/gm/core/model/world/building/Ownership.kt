package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.character.CharacterId
import kotlinx.serialization.Serializable

@Serializable
data class Ownership(
    val owner: Owner = UnknownOwner,
    val previousOwners: List<PreviousOwner> = emptyList(),
) {

    fun contains(character: CharacterId) = previousOwners.any { it.owner.contains(character) }

}
