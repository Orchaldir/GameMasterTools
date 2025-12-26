package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.beard.BeardType
import at.orchaldir.gm.core.model.character.appearance.hair.HairColorType
import at.orchaldir.gm.core.model.character.appearance.hair.HairType
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

val ALLOWED_HAIR_COLOR_TYPES = setOf(HairColorType.Normal, HairColorType.Exotic)

@Serializable
data class HairOptions(
    val beardTypes: OneOf<BeardType> = OneOf(BeardType.Normal),
    val hairTypes: OneOf<HairType> = OneOf(HairType.Normal),
    val colors: HairColorOptions = HairColorOptions(),
)
