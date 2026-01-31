package at.orchaldir.gm.core.model.rpg

import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentData(
    val damageModifier: SimpleModifiedDiceRange = SimpleModifiedDiceRange(IntRange(-2, 2), IntRange(-6, 6)),
    val maxDamageResistance: Int = 20,
    val damageResistanceModifier: IntRange = IntRange(-5, 5),
    val maxDefenseBonus: Int = 10,
    val defenseBonusModifier: IntRange = IntRange(-5, 5),
    val musclePoweredStatistic: StatisticId? = null,
    val skillModifier: IntRange = IntRange(-2, 2),
)
