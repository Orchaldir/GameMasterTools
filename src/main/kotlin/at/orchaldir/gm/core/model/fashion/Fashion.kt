package at.orchaldir.gm.core.model.fashion

import at.orchaldir.gm.core.model.appearance.OneOf
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
    val dresses: OneOf<ItemTemplateId> = OneOf(),
    val footwear: OneOf<ItemTemplateId> = OneOf(),
    val hats: OneOf<ItemTemplateId> = OneOf(),
    val pants: OneOf<ItemTemplateId> = OneOf(),
    val shirts: OneOf<ItemTemplateId> = OneOf(),
    val skirts: OneOf<ItemTemplateId> = OneOf(),
) : Element<FashionId> {

    override fun id() = id

}