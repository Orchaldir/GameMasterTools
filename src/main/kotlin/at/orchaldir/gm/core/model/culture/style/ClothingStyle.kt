package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.item.style.HatStyle
import kotlinx.serialization.Serializable

@Serializable
data class ClothingStyle(
    val hatStyles: OneOf<HatStyle> = OneOf(HatStyle.entries),
)
