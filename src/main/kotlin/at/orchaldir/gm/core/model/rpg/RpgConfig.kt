package at.orchaldir.gm.core.model.rpg

import kotlinx.serialization.Serializable

@Serializable
data class RpgConfig(
    val equipment: EquipmentConfig = EquipmentConfig(),
    val defaultDieType: DieType = DieType.D6,
    val damage: SimpleModifiedDiceRange = SimpleModifiedDiceRange(IntRange(0, 20), IntRange(-6, 6)),
)
