package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle
import at.orchaldir.gm.core.model.character.appearance.hair.HairLength
import at.orchaldir.gm.core.model.character.appearance.hair.HairStyle
import at.orchaldir.gm.core.model.character.appearance.hair.LongHairStyle
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

@Serializable
data class AppearanceStyle(
    val beardStyles: OneOf<BeardStyleType> = OneOf(BeardStyleType.GoateeAndMoustache),
    val goateeStyles: OneOf<GoateeStyle> = OneOf(GoateeStyle.Goatee),
    val moustacheStyles: OneOf<MoustacheStyle> = OneOf(MoustacheStyle.Handlebar),
    val hairStyles: OneOf<HairStyle> = OneOf(HairStyle.Short),
    val shortHairStyles: OneOf<ShortHairStyle> = OneOf(ShortHairStyle.MiddlePart),
    val longHairStyles: OneOf<LongHairStyle> = OneOf(LongHairStyle.Straight),
    val hairLengths: OneOf<HairLength> = OneOf(HairLength.entries),
    val lipColors: OneOf<Color> = OneOf(Color.Black),
)
