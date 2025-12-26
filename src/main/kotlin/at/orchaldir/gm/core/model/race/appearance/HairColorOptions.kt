package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.character.appearance.hair.HairColorType
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHairColorEnum
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.Serializable

@Serializable
data class HairColorOptions(
    val types: OneOf<HairColorType> = OneOf(HairColorType.Normal),
    val normal: OneOf<NormalHairColorEnum> = OneOf(NormalHairColorEnum.entries),
    val exotic: OneOf<Color> = OneOf(Color.entries),
)
