package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.hair.HairColorType
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHairColorEnum
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.Serializable

val DEFAULT_EXOTIC_HAIR_COLORS = setOf(Color.SaddleBrown)
val DEFAULT_NORMAL_HAIR_COLORS = NormalHairColorEnum.entries

@Serializable
data class HairColorOptions(
    val types: OneOf<HairColorType> = OneOf(HairColorType.Normal),
    val normal: OneOf<NormalHairColorEnum> = OneOf(DEFAULT_NORMAL_HAIR_COLORS),
    val exotic: OneOf<Color> = OneOf(DEFAULT_EXOTIC_HAIR_COLORS),
)
