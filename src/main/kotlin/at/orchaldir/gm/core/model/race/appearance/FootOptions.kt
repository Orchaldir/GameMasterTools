package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.FootType
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.Serializable

const val DEFAULT_CLAW_NUMBER = 3
val DEFAULT_CLAW_COLOR = Color.Black
val DEFAULT_CLAW_SIZE = Size.Medium

@Serializable
data class FootOptions(
    val footTypes: OneOf<FootType> = OneOf(FootType.Normal),
    val clawNumber: Int = DEFAULT_CLAW_NUMBER,
    val clawColors: OneOf<Color> = OneOf(DEFAULT_CLAW_COLOR),
    val clawSizes: OneOf<Size> = OneOf(DEFAULT_CLAW_SIZE),
)
