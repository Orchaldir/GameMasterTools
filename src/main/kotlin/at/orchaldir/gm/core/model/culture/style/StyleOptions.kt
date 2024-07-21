package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.OneOf
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle
import at.orchaldir.gm.core.model.character.appearance.hair.HairStyleType
import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import kotlinx.serialization.Serializable

@Serializable
data class StyleOptions(
    val beardStyles: OneOf<BeardStyleType> = OneOf(BeardStyleType.entries),
    val goateeStyles: OneOf<GoateeStyle> = OneOf(GoateeStyle.entries),
    val moustacheStyle: OneOf<MoustacheStyle> = OneOf(MoustacheStyle.entries),
    val hairStyles: OneOf<HairStyleType> = OneOf(HairStyleType.entries),
    val lipColors: OneOf<Color> = OneOf(Color.entries),
)
