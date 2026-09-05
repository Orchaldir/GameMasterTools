package at.orchaldir.gm.core.model.rpg.equipment

import kotlinx.serialization.Serializable

@Serializable
data class RangedWeaponStats(
    val type: RangedWeaponTypeId? = null,
    val modifiers: Set<EquipmentModifierId> = emptySet(),
)
