package at.orchaldir.gm.core.model.rpg.equipment

val EQUIPMENT_TYPE_CATEGORIES = EquipmentCategory.entries.toSet() - EquipmentCategory.Ammunition;

enum class EquipmentCategory {
    Generic,
    Ammunition,
    Armor,
    MeleeWeapons,
    RangedWeapons,
    Shields,
    Weapons;

    fun contains(other: EquipmentCategory) = when (this) {
        Generic -> true
        Weapons -> when (other) {
            MeleeWeapons -> true
            RangedWeapons -> true
            Weapons -> true
            else -> false
        }

        else -> this == other
    }
}