package at.orchaldir.gm.core.model.rpg

import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.model.util.quantity.ModifiedDiceRange
import at.orchaldir.gm.utils.math.RangeInt
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentConfig(
    val damageModifier: ModifiedDiceRange = ModifiedDiceRange(RangeInt(-2, 2), RangeInt(-6, 6)),
    val maxDamageResistance: Int = 20,
    val damageResistanceModifier: RangeInt = RangeInt(-5, 5),
    val maxDefenseBonus: Int = 10,
    val defenseBonusModifier: RangeInt = RangeInt(-5, 5),
    val musclePoweredStatistic: StatisticId? = null,
    val parryingModifier: RangeInt = RangeInt(-2, 2),
    val skillModifier: RangeInt = RangeInt(-2, 2),
)
