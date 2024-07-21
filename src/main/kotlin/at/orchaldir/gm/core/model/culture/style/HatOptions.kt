package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.appearance.ValueMap
import at.orchaldir.gm.core.model.appearance.SharedValue
import at.orchaldir.gm.core.model.item.style.HatStyle
import kotlinx.serialization.Serializable

@Serializable
data class HatOptions(
    val styles: OneOf<HatStyle> = OneOf(HatStyle.entries),
    val colors: ValueMap<HatStyle, OneOf<Color>> = SharedValue(OneOf(Color.entries)),
)
