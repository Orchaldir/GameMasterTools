package at.orchaldir.gm.core.model.character.statistic

import kotlinx.serialization.Serializable

@Serializable
data class Statblock(
    val statistics: Map<StatisticId, Int> = emptyMap(),
)
