package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.wing.DEFAULT_BIRD_COLOR
import at.orchaldir.gm.core.model.character.appearance.wing.DEFAULT_BUTTERFLY_COLOR
import at.orchaldir.gm.core.model.character.appearance.wing.WingType
import at.orchaldir.gm.core.model.character.appearance.wing.WingsLayout
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import kotlinx.serialization.Serializable

@Serializable
data class WingOptions(
    val layouts: OneOf<WingsLayout> = OneOf(WingsLayout.None),
    val types: OneOrNone<WingType> = OneOrNone(),
    val batColors: FeatureColorOptions = FeatureColorOptions(),
    val birdColors: OneOf<Color> = OneOf(DEFAULT_BIRD_COLOR),
    val butterflyColors: OneOf<Color> = OneOf(DEFAULT_BUTTERFLY_COLOR),
) {

    fun hasWings() = layouts.contains(WingsLayout.One) ||
            layouts.contains(WingsLayout.Two) ||
            layouts.contains(WingsLayout.Different)

}
