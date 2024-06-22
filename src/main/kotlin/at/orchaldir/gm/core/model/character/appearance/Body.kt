package at.orchaldir.gm.core.model.character.appearance

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
    val skin: Skin = NormalSkin(),
)