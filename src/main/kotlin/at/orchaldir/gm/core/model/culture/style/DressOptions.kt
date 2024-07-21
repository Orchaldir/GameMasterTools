package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.item.style.NecklineStyle
import at.orchaldir.gm.core.model.item.style.SkirtStyle
import at.orchaldir.gm.core.model.item.style.SleeveStyle
import kotlinx.serialization.Serializable

@Serializable
data class DressOptions(
    val colors: OneOf<Color> = OneOf(Color.entries),
    val skirtStyles: OneOf<SkirtStyle> = OneOf(SkirtStyle.entries),
    val necklineStyles: OneOf<NecklineStyle> = OneOf(NecklineStyle.entries),
    val sleeveStyles: OneOf<SleeveStyle> = OneOf(SleeveStyle.entries),
)
