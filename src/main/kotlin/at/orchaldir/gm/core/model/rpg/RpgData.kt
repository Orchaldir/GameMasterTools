package at.orchaldir.gm.core.model.rpg

import kotlinx.serialization.Serializable

@Serializable
data class RpgData(
    val defaultDieType: DieType = DieType.D6,
    val damageRange: SimpleModifiedDiceRange = SimpleModifiedDiceRange(Range(1, 20), Range(-6, 6)),
    val damageModifierRange: SimpleModifiedDiceRange = SimpleModifiedDiceRange(Range(-2, 2), Range(-6, 6)),
    val damageResistanceRange: Range = Range(-5, 5),
    val defenseBonusRange: Range = Range(-5, 5),
)
