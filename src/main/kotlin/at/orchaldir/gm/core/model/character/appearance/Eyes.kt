package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import kotlinx.serialization.SerialName
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

@Serializable
sealed class Eyes

@Serializable
@SerialName("None")
data object NoEyes : Eyes()

@Serializable
@SerialName("One")
data class OneEye(
    val eye: Eye = Eye(),
    val size: Size = Size.Medium,
) : Eyes()

@Serializable
@SerialName("Two")
data class TwoEyes(val eye: Eye = Eye()) : Eyes()

