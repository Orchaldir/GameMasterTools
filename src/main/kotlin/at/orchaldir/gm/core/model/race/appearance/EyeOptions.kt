package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.EnumRarity
import at.orchaldir.gm.core.model.character.appearance.EyeShape
import at.orchaldir.gm.core.model.character.appearance.PupilShape
import kotlinx.serialization.Serializable

@Serializable
data class EyeOptions(
    val eyeShapes: EnumRarity<EyeShape> = EnumRarity(EyeShape.entries),
    val pupilShapes: EnumRarity<PupilShape> = EnumRarity(PupilShape.entries),
    val pupilColors: EnumRarity<Color> = EnumRarity(Color.entries),
    val scleraColors: EnumRarity<Color> = EnumRarity(Color.entries),
)
