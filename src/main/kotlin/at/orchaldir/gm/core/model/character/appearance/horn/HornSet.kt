package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.Serializable

enum class HornSetShape {
    Goat,
    Mouflon,
    Sheep,
}

@Serializable
data class HornSet(
    val color: Color = Color.Black,
    val shape: HornSetShape = HornSetShape.Goat,
    val size: Size = Size.Medium,
)