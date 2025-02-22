package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.FootType
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.Serializable

@Serializable
data class FootOptions(
    val footTypes: OneOf<FootType> = OneOf(FootType.entries),
    val clawNumber: Int = 3,
    val clawColors: OneOf<Color> = OneOf(Color.entries),
    val clawSizes: OneOf<Size> = OneOf(Size.entries),
)
