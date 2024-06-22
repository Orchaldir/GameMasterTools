package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.appearance.Size
import kotlinx.serialization.Serializable

enum class BodyShape {
    Fat,
    Hourglass,
    Muscular,
    Rectangle,
}

@Serializable
data class Body(
    val bodyShape: BodyShape = BodyShape.Rectangle,
    val width: Size,
    val skin: Skin = NormalSkin(),
)