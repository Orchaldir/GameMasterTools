package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.appearance.Color
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
    val eyeShape: EyeShape,
    val pupilShape: PupilShape,
    val pupilColor: Color,
    val backgroundColor: Color,
)

@Serializable
sealed class Eyes
data object NoEyes : Eyes()
data class OneEye(val eye: Eye) : Eyes()
data class TwoEyes(val eye: Eye) : Eyes()

