package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.item.style.NecklineStyle
import at.orchaldir.gm.core.model.item.style.PantsStyle
import at.orchaldir.gm.core.model.item.style.SleeveStyle
import kotlinx.serialization.Serializable

@Serializable
data class PantsAndShirtOptions(
    val pantsColors: OneOf<Color> = OneOf(Color.entries),
    val pantsStyles: OneOf<PantsStyle> = OneOf(PantsStyle.entries),
    val necklineStyles: OneOf<NecklineStyle> = OneOf(NecklineStyle.entries),
    val shirtColors: OneOf<Color> = OneOf(Color.entries),
    val sleeveStyles: OneOf<SleeveStyle> = OneOf(SleeveStyle.entries),
)
