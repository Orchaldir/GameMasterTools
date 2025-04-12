package at.orchaldir.gm.core.model.culture.fashion

import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType.*
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

@Serializable
data class AppearanceStyle(
    val beardStyles: OneOf<BeardStyleType> = OneOf(BeardStyleType.entries),
    val goateeStyles: OneOf<GoateeStyle> = OneOf(GoateeStyle.entries),
    val moustacheStyles: OneOf<MoustacheStyle> = OneOf(MoustacheStyle.entries),
    val hairStyles: OneOf<HairStyle> = OneOf(HairStyle.entries),
    val bunStyles: OneOf<BunStyle> = OneOf(BunStyle.entries),
    val longHairStyles: OneOf<LongHairStyle> = OneOf(LongHairStyle.entries),
    val ponytailStyles: OneOf<PonytailStyle> = OneOf(PonytailStyle.entries),
    val ponytailPositions: OneOf<PonytailPosition> = OneOf(PonytailPosition.entries),
    val shortHairStyles: OneOf<ShortHairStyle> = OneOf(ShortHairStyle.entries),
    val hairLengths: OneOf<HairLength> = OneOf(HairLength.entries),
    val lipColors: OneOf<Color> = OneOf(Color.Black),
) {

    fun hasMoustache() =
        beardStyles.contains(Moustache) || beardStyles.contains(GoateeAndMoustache)

    fun hasGoatee() =
        beardStyles.contains(Goatee) || beardStyles.contains(GoateeAndMoustache)

    fun hasLongHair() =
        hairStyles.contains(HairStyle.Long) ||
                hairStyles.contains(HairStyle.Ponytail)

}
