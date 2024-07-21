package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.OneOf
import kotlinx.serialization.Serializable

@Serializable
data class PantsAndShirtOptions(
    val pantsColors: OneOf<Color> = OneOf(Color.entries),
    val shirtColors: OneOf<Color> = OneOf(Color.entries),
)
