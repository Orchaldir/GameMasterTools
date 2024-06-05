package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.core.model.character.appearance.EyeShape
import at.orchaldir.gm.core.model.character.appearance.PupilShape
import kotlinx.serialization.Serializable

@Serializable
data class EyeOptions(
    val eyeShapes: RarityMap<EyeShape> = RarityMap(EyeShape.entries),
    val pupilShapes: RarityMap<PupilShape> = RarityMap(PupilShape.entries),
    val pupilColors: RarityMap<Color> = RarityMap(Color.entries),
    val scleraColors: RarityMap<Color> = RarityMap(Color.entries),
)
