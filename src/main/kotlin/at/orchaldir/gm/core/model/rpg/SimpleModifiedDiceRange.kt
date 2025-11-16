package at.orchaldir.gm.core.model.rpg

import kotlinx.serialization.Serializable

@Serializable
data class SimpleModifiedDiceRange(
    val minDice: Int,
    val maxDice: Int,
    val minModifier: Int,
    val maxModifier: Int,
)
