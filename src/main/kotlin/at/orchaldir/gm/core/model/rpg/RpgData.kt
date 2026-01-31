package at.orchaldir.gm.core.model.rpg

import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import kotlinx.serialization.Serializable

@Serializable
data class RpgData(
    val equipment: EquipmentData = EquipmentData(),
    val defaultDieType: DieType = DieType.D6,
    val damage: SimpleModifiedDiceRange = SimpleModifiedDiceRange(IntRange(0, 20), IntRange(-6, 6)),
)
