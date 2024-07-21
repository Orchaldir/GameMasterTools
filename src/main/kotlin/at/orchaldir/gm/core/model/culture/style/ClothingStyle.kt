package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.appearance.OneOf
import kotlinx.serialization.Serializable

@Serializable
data class ClothingStyle(
    val clothingSets: OneOf<ClothingSet> = OneOf(ClothingSet.entries),
    val pantsAndShirt: PantsAndShirtOptions = PantsAndShirtOptions(),
)
