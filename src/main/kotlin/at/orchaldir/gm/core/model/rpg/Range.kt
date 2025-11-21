package at.orchaldir.gm.core.model.rpg

import kotlinx.serialization.Serializable

@Serializable
data class Range(
    val min: Int,
    val max: Int,
)
