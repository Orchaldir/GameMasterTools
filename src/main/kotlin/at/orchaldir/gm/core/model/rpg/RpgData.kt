package at.orchaldir.gm.core.model.rpg

import kotlinx.serialization.Serializable

@Serializable
data class RpgData(
    val defaultDieType: DieType = DieType.D6,
    val damageRange: SimpleModifiedDiceRange = SimpleModifiedDiceRange(1, 20, -6, 6),
    val damageModifierRange: SimpleModifiedDiceRange = SimpleModifiedDiceRange(-2, 2, -6, 6),
)
