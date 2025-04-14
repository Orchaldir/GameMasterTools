package at.orchaldir.gm.core.model.culture.fashion

import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType.*
import at.orchaldir.gm.core.model.character.appearance.beard.FullBeardStyle
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle
import at.orchaldir.gm.core.model.character.appearance.hair.HairLength
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import kotlinx.serialization.Serializable

@Serializable
data class BeardFashion(
    val beardStyles: OneOf<BeardStyleType> = OneOf(BeardStyleType.entries),
    val beardLength: OneOrNone<HairLength> = OneOrNone(HairLength.entries),
    val fullBeardStyles: OneOrNone<FullBeardStyle> = OneOrNone(FullBeardStyle.entries),
    val goateeStyles: OneOrNone<GoateeStyle> = OneOrNone(GoateeStyle.entries),
    val moustacheStyles: OneOrNone<MoustacheStyle> = OneOrNone(MoustacheStyle.entries),
) {

    fun hasMoustache() =
        beardStyles.contains(Moustache) || beardStyles.contains(GoateeAndMoustache)

    fun hasGoatee() =
        beardStyles.contains(Goatee) || beardStyles.contains(GoateeAndMoustache)

}
