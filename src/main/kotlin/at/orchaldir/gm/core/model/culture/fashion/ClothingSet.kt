package at.orchaldir.gm.core.model.culture.fashion

import at.orchaldir.gm.core.model.item.equipment.EquipmentDataType

enum class ClothingSet {
    Dress,
    PantsAndShirt,
    ShirtAndSkirt,
    Suit;

    fun getTypes() = when (this) {
        Dress -> setOf(EquipmentDataType.Dress)
        PantsAndShirt -> setOf(EquipmentDataType.Pants, EquipmentDataType.Shirt)
        ShirtAndSkirt -> setOf(EquipmentDataType.Skirt, EquipmentDataType.Shirt)
        Suit -> setOf(EquipmentDataType.Pants, EquipmentDataType.Shirt, EquipmentDataType.SuitJacket)
    }
}