package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.item.ItemTemplateId
import kotlinx.serialization.Serializable

@Serializable
data class ClothingStyle(
    val clothingSets: OneOf<ClothingSet> = OneOf(ClothingSet.entries),
    val dresses: OneOf<ItemTemplateId> = OneOf(),
    val footwear: OneOf<ItemTemplateId> = OneOf(),
    val hats: OneOf<ItemTemplateId> = OneOf(),
    val pants: OneOf<ItemTemplateId> = OneOf(),
    val shirts: OneOf<ItemTemplateId> = OneOf(),
    val skirts: OneOf<ItemTemplateId> = OneOf(),
)
