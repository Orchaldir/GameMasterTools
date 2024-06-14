package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.RarityMap
import kotlinx.serialization.Serializable

@Serializable
data class HairOptions(
    val beardTypes: RarityMap<BeardType> = RarityMap(BeardType.entries),
    val hairTypes: RarityMap<HairType> = RarityMap(HairType.entries),
    val colors: RarityMap<Color> = RarityMap(Color.entries),
)
