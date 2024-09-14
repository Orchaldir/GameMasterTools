package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.Serializable

private val MALE_SHAPES = listOf(BodyShape.Fat, BodyShape.Muscular, BodyShape.Rectangle)

enum class BodyShape {
    Fat,
    Hourglass,
    Muscular,
    Rectangle,
}

fun getAvailableBodyShapes(gender: Gender) = if (gender == Gender.Male) {
    MALE_SHAPES
} else {
    BodyShape.entries
}

@Serializable
data class Body(
    val bodyShape: BodyShape = BodyShape.Rectangle,
    val width: Size = Size.Medium,
    val skin: Skin = NormalSkin(),
)