package at.orchaldir.gm.core.model.fashion

import at.orchaldir.gm.core.model.item.equipment.EquipmentType

enum class ClothingSet {
    Dress,
    PantsAndShirt,
    ShirtAndSkirt,
    Suit;

    fun getTypes() = when (this) {
        Dress -> setOf(EquipmentType.Dress)
        PantsAndShirt -> setOf(EquipmentType.Pants, EquipmentType.Shirt)
        ShirtAndSkirt -> setOf(EquipmentType.Skirt, EquipmentType.Shirt)
        Suit -> setOf(EquipmentType.Coat, EquipmentType.Pants, EquipmentType.Shirt)
    }
}