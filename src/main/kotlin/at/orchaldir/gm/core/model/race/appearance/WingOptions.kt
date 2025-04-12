package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

@Serializable
data class WingOptions(
    val layouts: OneOf<WingsLayout> = OneOf(WingsLayout.None),
    val types: OneOf<WingType> = OneOf(WingType.Bird),
    val batColors: FeatureColorOptions = FeatureColorOptions(),
    val birdColors: OneOf<Color> = OneOf(DEFAULT_BIRD_COLOR),
    val butterflyColors: OneOf<Color> = OneOf(DEFAULT_BUTTERFLY_COLOR),
)
