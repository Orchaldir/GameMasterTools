package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.horn.HornsLayout
import at.orchaldir.gm.core.model.character.appearance.wing.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

@Serializable
data class HornOptions(
    val layouts: OneOf<HornsLayout> = OneOf(HornsLayout.None),
    val types: OneOf<WingType> = OneOf(WingType.Bird),
    val batColors: OneOf<Color> = OneOf(DEFAULT_BAT_COLOR),
    val birdColors: OneOf<Color> = OneOf(DEFAULT_BIRD_COLOR),
    val butterflyColors: OneOf<Color> = OneOf(DEFAULT_BUTTERFLY_COLOR),
)
