package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.FeatureColorType
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

val DEFAULT_SIMPLE_TAIL_COLOR = Color.SaddleBrown

@Serializable
data class FeatureColorOptions(
    val types: FeatureColorType = FeatureColorType.Overwrite,
    val colors: OneOf<Color> = OneOf(DEFAULT_SIMPLE_TAIL_COLOR),
)
