package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.horn.DEFAULT_HORN_COLOR
import at.orchaldir.gm.core.model.character.appearance.horn.HornShapeType
import at.orchaldir.gm.core.model.character.appearance.horn.HornsLayout
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

@Serializable
data class HornOptions(
    val layouts: OneOf<HornsLayout> = OneOf(HornsLayout.None),
    val shapes: OneOf<HornShapeType> = OneOf(HornShapeType.Straight),
    val colors: OneOf<Color> = OneOf(DEFAULT_HORN_COLOR),
    val crownFront: OneOf<Int> = OneOf(2),
    val crownBack: OneOf<Int> = OneOf(2),
)
