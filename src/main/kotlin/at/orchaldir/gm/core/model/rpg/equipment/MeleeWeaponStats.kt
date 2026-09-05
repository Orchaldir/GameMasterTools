package at.orchaldir.gm.core.model.rpg.equipment

import kotlinx.serialization.Serializable

@Serializable
data class MeleeWeaponStats(
    val type: MeleeWeaponTypeId? = null,
    val modifiers: Set<EquipmentModifierId> = emptySet(),
)
