package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.character.appearance.EarShape
import at.orchaldir.gm.core.model.character.appearance.SkinColor
import kotlinx.serialization.Serializable

@Serializable
data class AppearanceOptions(
    val appearanceType: OneOf<AppearanceType> = OneOf(AppearanceType.entries),
    val skinTypes: OneOf<SkinType> = OneOf(SkinType.entries),
    val scalesColors: OneOf<Color> = OneOf(Color.entries),
    val normalSkinColors: OneOf<SkinColor> = OneOf(SkinColor.entries),
    val exoticSkinColors: OneOf<Color> = OneOf(Color.entries),
    val earsLayout: OneOf<EarsLayout> = OneOf(EarsLayout.entries),
    val earShapes: OneOf<EarShape> = OneOf(EarShape.entries),
    val eyesLayout: OneOf<EyesLayout> = OneOf(EyesLayout.entries),
    val eyeOptions: EyeOptions = EyeOptions(),
    val hairOptions: HairOptions = HairOptions(),
    val mouthTypes: OneOf<MouthType> = OneOf(MouthType.entries),
)
