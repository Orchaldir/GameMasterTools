package at.orchaldir.gm.core.model.rpg.equipment

import kotlinx.serialization.Serializable

@Serializable
data class ArmorStats(
    val type: EquipmentTypeId? = null,
    val modifiers: Set<EquipmentModifierId> = emptySet(),
)
