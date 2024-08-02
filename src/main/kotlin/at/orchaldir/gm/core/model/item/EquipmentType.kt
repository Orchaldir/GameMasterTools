package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.item.EquipmentSlot.*
import at.orchaldir.gm.core.model.item.EquipmentType.Footwear
import at.orchaldir.gm.core.model.item.EquipmentType.Gloves
import at.orchaldir.gm.core.model.item.EquipmentType.Hat

val ACCESSORIES = setOf(Footwear, Gloves, Hat)
val NOT_NONE = EquipmentType.entries.toSet() - EquipmentType.None

enum class EquipmentType {
    None,
    Coat,
    Dress,
    Footwear,
    Gloves,
    Hat,
    Pants,
    Shirt,
    Skirt;

    fun slots(): Set<EquipmentSlot> = when (this) {
        None -> emptySet()
        Coat -> setOf(Outerwear)
        Dress -> setOf(Bottom, Top)
        Footwear -> setOf(Foot)
        Gloves -> setOf(Handwear)
        Hat -> setOf(Headwear)
        Pants -> setOf(Bottom)
        Shirt -> setOf(Top)
        Skirt -> setOf(Bottom)
    }
}