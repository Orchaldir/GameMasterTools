package at.orchaldir.gm.core.model.character.appearance.eye

import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.Serializable

@Serializable
enum class EyeShape {
    Almond,
    Circle,
    Ellipse,
}

@Serializable
enum class PupilShape {
    Circle,
    HorizontalSlit,
    VerticalSlit,
}

@Serializable
data class Eye(
    val eyeShape: EyeShape = EyeShape.Circle,
    val pupilShape: PupilShape = PupilShape.Circle,
    val pupilColor: Color = Color.Green,
    val scleraColor: Color = Color.White,
)
