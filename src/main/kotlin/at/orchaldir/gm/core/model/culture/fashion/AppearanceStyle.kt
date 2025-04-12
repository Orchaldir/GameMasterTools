package at.orchaldir.gm.core.model.culture.fashion

import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType.*
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import kotlinx.serialization.Serializable

@Serializable
data class AppearanceStyle(
    val beardStyles: OneOf<BeardStyleType> = OneOf(BeardStyleType.entries),
    val goateeStyles: OneOrNone<GoateeStyle> = OneOrNone(GoateeStyle.entries),
    val moustacheStyles: OneOrNone<MoustacheStyle> = OneOrNone(MoustacheStyle.entries),
    val hairStyles: OneOf<HairStyle> = OneOf(HairStyle.entries),
    val bunStyles: OneOrNone<BunStyle> = OneOrNone(BunStyle.entries),
    val longHairStyles: OneOrNone<LongHairStyle> = OneOrNone(LongHairStyle.entries),
    val ponytailStyles: OneOrNone<PonytailStyle> = OneOrNone(PonytailStyle.entries),
    val ponytailPositions: OneOrNone<PonytailPosition> = OneOrNone(PonytailPosition.entries),
    val shortHairStyles: OneOrNone<ShortHairStyle> = OneOrNone(ShortHairStyle.entries),
    val hairLengths: OneOrNone<HairLength> = OneOrNone(HairLength.entries),
    val lipColors: OneOf<Color> = OneOf(Color.entries),
) {

    fun hasMoustache() =
        beardStyles.contains(Moustache) || beardStyles.contains(GoateeAndMoustache)

    fun hasGoatee() =
        beardStyles.contains(Goatee) || beardStyles.contains(GoateeAndMoustache)

    fun hasLongHair() =
        hairStyles.contains(HairStyle.Long) ||
                hairStyles.contains(HairStyle.Ponytail)

}
