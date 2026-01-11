package at.orchaldir.gm.core.model.rpg

import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import kotlinx.serialization.Serializable

@Serializable
data class RpgData(
    val defaultDieType: DieType = DieType.D6,
    val damage: SimpleModifiedDiceRange = SimpleModifiedDiceRange(Range(0, 20), Range(-6, 6)),
    val damageModifier: SimpleModifiedDiceRange = SimpleModifiedDiceRange(Range(-2, 2), Range(-6, 6)),
    val maxDamageResistance: Int = 20,
    val damageResistanceModifier: Range = Range(-5, 5),
    val maxDefenseBonus: Int = 10,
    val defenseBonusModifier: Range = Range(-5, 5),
    val musclePoweredStatistic: StatisticId? = null,
)
