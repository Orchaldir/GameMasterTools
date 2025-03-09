package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.horn.DEFAULT_HORN_COLOR
import at.orchaldir.gm.core.model.character.appearance.horn.HornsLayout
import at.orchaldir.gm.core.model.character.appearance.horn.SimpleHornType
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

@Serializable
data class HornOptions(
    val layouts: OneOf<HornsLayout> = OneOf(HornsLayout.None),
    val simpleTypes: OneOf<SimpleHornType> = OneOf(SimpleHornType.Mouflon),
    val simpleLengths: Map<SimpleHornType, Factor> = emptyMap(),
    val colors: OneOf<Color> = OneOf(DEFAULT_HORN_COLOR),
    val crownFront: OneOf<Int> = OneOf(2),
    val crownBack: OneOf<Int> = OneOf(2),
)
