package at.orchaldir.gm.core.model.rpg

import at.orchaldir.gm.core.model.rpg.dice.DieType
import at.orchaldir.gm.core.model.rpg.dice.ModifiedDiceRange
import kotlinx.serialization.Serializable

@Serializable
data class RpgConfig(
    val equipment: EquipmentConfig = EquipmentConfig(),
    val defaultDieType: DieType = DieType.D6,
    val damage: ModifiedDiceRange = ModifiedDiceRange(IntRange(0, 20), IntRange(-6, 6)),
)
