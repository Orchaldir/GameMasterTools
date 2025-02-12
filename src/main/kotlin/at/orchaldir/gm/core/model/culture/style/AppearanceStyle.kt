package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.character.appearance.beard.BeardStyleType
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle
import at.orchaldir.gm.core.model.character.appearance.hair.HairStyleType
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

@Serializable
data class AppearanceStyle(
    val beardStyles: OneOf<BeardStyleType> = OneOf(BeardStyleType.GoateeAndMoustache),
    val goateeStyles: OneOf<GoateeStyle> = OneOf(GoateeStyle.Goatee),
    val moustacheStyles: OneOf<MoustacheStyle> = OneOf(MoustacheStyle.Handlebar),
    val hairStyles: OneOf<HairStyleType> = OneOf(HairStyleType.SidePart),
    val lipColors: OneOf<Color> = OneOf(Color.Black),
)
