package at.orchaldir.gm.core.model.fashion

import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.appearance.OneOrNone
import at.orchaldir.gm.core.model.item.EquipmentType
import at.orchaldir.gm.core.model.item.ItemTemplateId
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class FashionId(val value: Int) : Id<FashionId> {

    override fun next() = FashionId(value + 1)
    override fun value() = value

}

@Serializable
data class Fashion(
    val id: FashionId,
    val name: String = "Fashion ${id.value}",
    val clothingSets: OneOf<ClothingSet> = OneOf(ClothingSet.entries),
    val dresses: OneOrNone<ItemTemplateId> = OneOrNone(),
    val footwear: OneOrNone<ItemTemplateId> = OneOrNone(),
    val hats: OneOrNone<ItemTemplateId> = OneOrNone(),
    val pants: OneOrNone<ItemTemplateId> = OneOrNone(),
    val shirts: OneOrNone<ItemTemplateId> = OneOrNone(),
    val skirts: OneOrNone<ItemTemplateId> = OneOrNone(),
) : Element<FashionId> {

    override fun id() = id

    fun getAllItemTemplates() = EquipmentType.entries
        .flatMap { getOptions(it).getValidValues().keys }
        .toSet()

    fun getOptions(type: EquipmentType) = when (type) {
        EquipmentType.None -> OneOrNone()
        EquipmentType.Dress -> dresses
        EquipmentType.Footwear -> footwear
        EquipmentType.Hat -> hats
        EquipmentType.Pants -> pants
        EquipmentType.Shirt -> shirts
        EquipmentType.Skirt -> skirts
    }

}