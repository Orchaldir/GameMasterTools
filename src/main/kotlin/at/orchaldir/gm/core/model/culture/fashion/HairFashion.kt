package at.orchaldir.gm.core.model.culture.fashion

import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import kotlinx.serialization.Serializable

@Serializable
data class HairFashion(
    val hairStyles: OneOf<HairStyle> = OneOf(HairStyle.entries),
    val bunStyles: OneOrNone<BunStyle> = OneOrNone(BunStyle.entries),
    val longHairStyles: OneOrNone<LongHairStyle> = OneOrNone(LongHairStyle.entries),
    val ponytailStyles: OneOrNone<PonytailStyle> = OneOrNone(PonytailStyle.entries),
    val ponytailPositions: OneOrNone<PonytailPosition> = OneOrNone(PonytailPosition.entries),
    val shortHairStyles: OneOrNone<ShortHairStyle> = OneOrNone(ShortHairStyle.entries),
    val hairLengths: OneOrNone<HairLength> = OneOrNone(HairLength.entries),
) {

    fun hasLongHair() =
        hairStyles.contains(HairStyle.Long) ||
                hairStyles.contains(HairStyle.Ponytail)

}
