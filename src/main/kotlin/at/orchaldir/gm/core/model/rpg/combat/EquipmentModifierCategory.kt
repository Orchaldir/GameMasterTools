package at.orchaldir.gm.core.model.rpg.combat

enum class EquipmentModifierCategory {
    All,
    Ammunition,
    Armor,
    MeleeWeapons,
    RangedWeapons,
    Shields,
    Weapons;

    fun contains(category: EquipmentModifierCategory) = when (this) {
        All -> true
        Weapons -> when (category) {
            MeleeWeapons -> true
            RangedWeapons -> true
            Weapons -> true
            else -> false
        }
        else -> this == category
    }
}