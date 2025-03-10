package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.horn.DEFAULT_HORN_COLOR
import at.orchaldir.gm.core.model.character.appearance.horn.HornsLayout
import at.orchaldir.gm.core.model.character.appearance.horn.SimpleHornType
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

val DEFAULT_CROWN_HORNS = 2
val DEFAULT_SIMPLE_LENGTH = FULL
val DEFAULT_CROWN_LENGTH = Factor(0.2f)

@Serializable
data class HornOptions(
    val layouts: OneOf<HornsLayout> = OneOf(HornsLayout.None),
    val simpleTypes: OneOf<SimpleHornType> = OneOf(SimpleHornType.Mouflon),
    val simpleLength: Factor = DEFAULT_SIMPLE_LENGTH,
    val colors: OneOf<Color> = OneOf(DEFAULT_HORN_COLOR),
    val crownLength: Factor = DEFAULT_CROWN_LENGTH,
    val crownFront: OneOf<Int> = OneOf(DEFAULT_CROWN_HORNS),
    val crownBack: OneOf<Int> = OneOf(DEFAULT_CROWN_HORNS),
)
