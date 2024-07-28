package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.item.EquipmentSlot.*

enum class EquipmentType {
    None,
    Dress,
    Footwear,
    Gloves,
    Hat,
    Pants,
    Shirt,
    Skirt;

    fun slots(): Set<EquipmentSlot> = when (this) {
        None -> emptySet()
        Dress -> setOf(Bottom, Top)
        Footwear -> setOf(Foot)
        Gloves -> setOf(Headwear)
        Hat -> setOf(Headwear)
        Pants -> setOf(Bottom)
        Shirt -> setOf(Top)
        Skirt -> setOf(Bottom)
    }
}