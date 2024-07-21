package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.appearance.OneOf
import kotlinx.serialization.Serializable

@Serializable
data class ClothingStyle(
    val clothingSets: OneOf<ClothingSet> = OneOf(ClothingSet.entries),
    val dress: DressOptions = DressOptions(),
    val footwear: FootwearOptions = FootwearOptions(),
    val hat: HatOptions = HatOptions(),
    val pantsAndShirt: PantsAndShirtOptions = PantsAndShirtOptions(),
    val shirtAndSkirt: ShirtAndSkirtOptions = ShirtAndSkirtOptions(),
)
