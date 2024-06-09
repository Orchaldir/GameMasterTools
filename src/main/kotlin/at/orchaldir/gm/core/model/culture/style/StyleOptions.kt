package at.orchaldir.gm.core.model.culture.style

import at.orchaldir.gm.core.model.appearance.RarityMap
import kotlinx.serialization.Serializable

@Serializable
data class StyleOptions(
    val hairStyle: RarityMap<HairStyleType> = RarityMap(HairStyleType.entries),
)
