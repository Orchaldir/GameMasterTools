package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.core.model.character.appearance.EarShape
import at.orchaldir.gm.core.model.character.appearance.SkinColor
import kotlinx.serialization.Serializable

@Serializable
data class AppearanceOptions(
    val skinTypes: RarityMap<SkinType> = RarityMap(SkinType.entries),
    val scalesColors: RarityMap<Color> = RarityMap(Color.entries),
    val normalSkinColors: RarityMap<SkinColor> = RarityMap(SkinColor.entries),
    val exoticSkinColors: RarityMap<Color> = RarityMap(Color.entries),
    val earsLayout: RarityMap<EarsLayout> = RarityMap(EarsLayout.entries),
    val earShapes: RarityMap<EarShape> = RarityMap(EarShape.entries),
    val eyesLayout: RarityMap<EyesLayout> = RarityMap(EyesLayout.entries),
    val eyeOptions: EyeOptions = EyeOptions(),
    val hairOptions: HairOptions = HairOptions(),
    val mouthTypes: RarityMap<MouthType> = RarityMap(MouthType.entries),
)
