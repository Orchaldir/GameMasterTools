package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.Serializable

@Serializable
data class MeleeWeapon(
    val type: MeleeWeaponTypeId? = null,
    val modifiers: Set<MeleeWeaponModifierId> = emptySet(),
)
