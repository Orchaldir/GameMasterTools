package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.EnumRarity
import at.orchaldir.gm.core.model.character.appearance.EarShape
import at.orchaldir.gm.core.model.character.appearance.SkinColor
import kotlinx.serialization.Serializable

@Serializable
data class AppearanceOptions(
    val scalesColors: EnumRarity<Color> = EnumRarity(Color.entries),
    val normalSkinColors: EnumRarity<SkinColor> = EnumRarity(SkinColor.entries),
    val exoticSkinColors: EnumRarity<Color> = EnumRarity(Color.entries),
    val earShapes: EnumRarity<EarShape> = EnumRarity(EarShape.entries),
    val eyesOptions: EnumRarity<EyesOptions> = EnumRarity(EyesOptions.entries),
    val eyeOptions: EyeOptions = EyeOptions(),
)