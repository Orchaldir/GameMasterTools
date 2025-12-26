package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.Serializable

@Serializable
data class HairOptions(
    val beardTypes: OneOf<BeardType> = OneOf(BeardType.Normal),
    val hairTypes: OneOf<HairType> = OneOf(HairType.Exotic),
    val colors: OneOf<Color> = OneOf(Color.SaddleBrown),
)
