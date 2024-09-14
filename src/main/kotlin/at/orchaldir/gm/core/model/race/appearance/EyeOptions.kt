package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.character.appearance.EyeShape
import at.orchaldir.gm.core.model.character.appearance.PupilShape
import kotlinx.serialization.Serializable

@Serializable
data class EyeOptions(
    val eyeShapes: OneOf<EyeShape> = OneOf(EyeShape.entries),
    val pupilShapes: OneOf<PupilShape> = OneOf(PupilShape.entries),
    val pupilColors: OneOf<Color> = OneOf(Color.entries),
    val scleraColors: OneOf<Color> = OneOf(Color.entries),
)
