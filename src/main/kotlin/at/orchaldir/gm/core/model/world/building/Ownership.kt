package at.orchaldir.gm.core.model.world.building

import kotlinx.serialization.Serializable

@Serializable
data class Ownership(
    val owner: Owner = UnknownOwner,
    val previousOwners: List<PreviousOwner> = emptyList(),
)
