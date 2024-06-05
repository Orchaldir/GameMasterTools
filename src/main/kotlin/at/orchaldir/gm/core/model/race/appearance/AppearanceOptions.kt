package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.core.model.character.appearance.EarShape
import at.orchaldir.gm.core.model.character.appearance.SkinColor
import kotlinx.serialization.Serializable

@Serializable
data class AppearanceOptions(
    val scalesColors: RarityMap<Color> = RarityMap(Color.entries),
    val normalSkinColors: RarityMap<SkinColor> = RarityMap(SkinColor.entries),
    val exoticSkinColors: RarityMap<Color> = RarityMap(Color.entries),
    val earShapes: RarityMap<EarShape> = RarityMap(EarShape.entries),
    val eyesLayout: RarityMap<EyesLayout> = RarityMap(EyesLayout.entries),
    val eyeOptions: EyeOptions = EyeOptions(),
    val mouthTypes: RarityMap<MouthType> = RarityMap(MouthType.entries),
)
