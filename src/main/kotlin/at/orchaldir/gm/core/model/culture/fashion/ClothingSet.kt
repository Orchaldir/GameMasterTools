package at.orchaldir.gm.core.model.culture.fashion

import at.orchaldir.gm.core.model.item.equipment.EquipmentAppearanceType

enum class ClothingSet {
    Dress,
    Naked,
    PantsAndShirt,
    PantsAndTunic,
    ShirtAndSkirt,
    Suit;

    fun getTypes() = when (this) {
        Dress -> setOf(EquipmentAppearanceType.Dress)
        Naked -> emptySet()
        PantsAndShirt -> setOf(EquipmentAppearanceType.Pants, EquipmentAppearanceType.Shirt)
        PantsAndTunic -> setOf(EquipmentAppearanceType.Pants, EquipmentAppearanceType.Tunic)
        ShirtAndSkirt -> setOf(EquipmentAppearanceType.Skirt, EquipmentAppearanceType.Shirt)
        Suit -> setOf(EquipmentAppearanceType.Pants, EquipmentAppearanceType.Shirt, EquipmentAppearanceType.SuitJacket)
    }
}