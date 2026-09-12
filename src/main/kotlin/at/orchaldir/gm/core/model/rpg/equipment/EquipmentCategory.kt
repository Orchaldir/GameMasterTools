package at.orchaldir.gm.core.model.rpg.equipment

val EQUIPMENT_TYPE_CATEGORIES = EquipmentCategory.entries.toSet() - EquipmentCategory.Ammunition;

enum class EquipmentCategory {
    Generic,
    Ammunition,
    Armor,
    MeleeWeapon,
    RangedWeapon,
    Shield,
    Weapon;

    fun contains(other: EquipmentCategory) = when (this) {
        Generic -> true
        Weapon -> when (other) {
            MeleeWeapon -> true
            RangedWeapon -> true
            Weapon -> true
            else -> false
        }

        else -> this == other
    }
}