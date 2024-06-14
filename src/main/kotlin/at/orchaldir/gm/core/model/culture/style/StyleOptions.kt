package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.core.model.character.appearance.beard.GoateeStyle
import at.orchaldir.gm.core.model.character.appearance.beard.MoustacheStyle
import at.orchaldir.gm.core.model.race.appearance.BeardStyleType
import kotlinx.serialization.Serializable

@Serializable
data class StyleOptions(
    val beardStyles: RarityMap<BeardStyleType> = RarityMap(BeardStyleType.entries),
    val goateeStyles: RarityMap<GoateeStyle> = RarityMap(GoateeStyle.entries),
    val moustacheStyle: RarityMap<MoustacheStyle> = RarityMap(MoustacheStyle.entries),
    val hairStyles: RarityMap<HairStyleType> = RarityMap(HairStyleType.entries),
    val lipColors: RarityMap<Color> = RarityMap(Color.entries),
)
