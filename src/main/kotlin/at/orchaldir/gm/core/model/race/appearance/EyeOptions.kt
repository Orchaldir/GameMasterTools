package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.eye.EyeShape
import at.orchaldir.gm.core.model.character.appearance.eye.EyeType
import at.orchaldir.gm.core.model.character.appearance.eye.PupilShape
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.Serializable

@Serializable
data class EyeOptions(
    val eyeTypes: OneOf<EyeType> = OneOf(EyeType.Normal),
    val eyeShapes: OneOf<EyeShape> = OneOf(EyeShape.Circle),
    val eyeColors: OneOf<Color> = OneOf(Color.SaddleBrown),
    val pupilShapes: OneOf<PupilShape> = OneOf(PupilShape.Circle),
    val scleraColors: OneOf<Color> = OneOf(Color.White),
)
