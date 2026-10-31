package at.orchaldir.gm.core.model.rpg.statistic

import at.orchaldir.gm.core.model.rpg.equipment.EquipmentConfig
import at.orchaldir.gm.core.model.util.quantity.DieType
import at.orchaldir.gm.core.model.util.quantity.ModifiedDiceRange
import at.orchaldir.gm.utils.math.RangeInt
import kotlinx.serialization.Serializable

@Serializable
data class StatisticConfig(
    val raceLimits: StatisticLimits = StatisticLimits(),
    val characterLimits: StatisticLimits = StatisticLimits(),
)
