package at.orchaldir.gm.core.model.rpg.statistic

import kotlinx.serialization.Serializable

@Serializable
data class StatisticConfig(
    val raceLimits: StatisticLimits = StatisticLimits(),
    val characterLimits: StatisticLimits = StatisticLimits(),
) {

    fun getLimit(layer: StatisticLimitLayer) = when (layer) {
        StatisticLimitLayer.Race -> raceLimits
        StatisticLimitLayer.Character -> characterLimits
    }
}
