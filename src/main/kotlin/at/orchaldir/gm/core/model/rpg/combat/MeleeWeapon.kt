package at.orchaldir.gm.core.model.rpg.combat

data class MeleeWeapon(
    val type: MeleeWeaponTypeId? = null,
    val modifiers: Set<MeleeWeaponModifierId> = emptySet(),
)
