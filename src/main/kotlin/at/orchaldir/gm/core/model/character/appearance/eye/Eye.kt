package at.orchaldir.gm.core.model.character.appearance.eye

import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EyeType {
    Simple,
    Normal,
}

@Serializable
sealed class Eye {

    fun getShape() = when (this) {
        is SimpleEye -> eyeShape
        is NormalEye -> eyeShape
    }

    fun getType() = when (this) {
        is SimpleEye -> EyeType.Simple
        is NormalEye -> EyeType.Normal
    }

}

@Serializable
@SerialName("Simple")
data class SimpleEye(
    val eyeShape: EyeShape = EyeShape.Circle,
    val color: Color = Color.White,
) : Eye()

@Serializable
@SerialName("Normal")
data class NormalEye(
    val eyeShape: EyeShape = EyeShape.Circle,
    val pupilShape: PupilShape = PupilShape.Circle,
    val pupilColor: Color = Color.Green,
    val scleraColor: Color = Color.White,
) : Eye()
