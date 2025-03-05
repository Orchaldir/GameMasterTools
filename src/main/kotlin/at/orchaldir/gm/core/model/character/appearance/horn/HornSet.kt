package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.Serializable

enum class HornSetShape {
    Bull,
    Goat,
    Mouflon,
    Sheep,
}

@Serializable
data class HornSet(
    val shape: HornSetShape = HornSetShape.Goat,
    val size: Size = Size.Medium,
    val color: Color = Color.White,
)