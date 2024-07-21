package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.appearance.ValueMap
import at.orchaldir.gm.core.model.appearance.SharedValue
import at.orchaldir.gm.core.model.item.style.FootwearStyle
import kotlinx.serialization.Serializable

@Serializable
data class FootwearOptions(
    val styles: OneOf<FootwearStyle> = OneOf(FootwearStyle.entries),
    val colors: ValueMap<FootwearStyle, OneOf<Color>> = SharedValue(OneOf(Color.entries)),
    val soles: ValueMap<FootwearStyle, OneOf<Color>> = SharedValue(OneOf(Color.entries)),
)
