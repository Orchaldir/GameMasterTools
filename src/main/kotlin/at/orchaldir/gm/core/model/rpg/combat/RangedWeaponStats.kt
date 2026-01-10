package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.Serializable

@Serializable
data class RangedWeaponStats(
    val type: RangedWeaponTypeId? = null,
    val modifiers: Set<EquipmentModifierId> = emptySet(),
)
