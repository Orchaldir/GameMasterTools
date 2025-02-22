package at.orchaldir.gm.core.model.character.appearance.eye

import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.Serializable

@Serializable
data class Eye(
    val eyeShape: EyeShape = EyeShape.Circle,
    val pupilShape: PupilShape = PupilShape.Circle,
    val pupilColor: Color = Color.Green,
    val scleraColor: Color = Color.White,
)
