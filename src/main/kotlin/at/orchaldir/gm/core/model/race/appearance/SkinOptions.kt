package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.SkinColor
import at.orchaldir.gm.core.model.character.appearance.SkinType
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.OneOrNone
import kotlinx.serialization.Serializable

val DEFAULT_SKIN_TYPE = SkinType.Normal
val DEFAULT_FUR_COLOR = Color.SaddleBrown
val DEFAULT_SCALE_COLOR = Color.Red
val DEFAULT_EXOTIC_COLOR = Color.Green

@Serializable
data class SkinOptions(
    val skinTypes: OneOf<SkinType> = OneOf(DEFAULT_SKIN_TYPE),
    val exoticColors: OneOf<Color> = OneOf(DEFAULT_EXOTIC_COLOR),
    val furColors: OneOf<Color> = OneOf(DEFAULT_FUR_COLOR),
    val materialColors: OneOrNone<Color> = OneOrNone(),
    val materialIds: OneOf<MaterialId> = OneOf(MaterialId(0)),
    val normalColors: OneOf<SkinColor> = OneOf(SkinColor.entries),
    val scalesColors: OneOf<Color> = OneOf(DEFAULT_SCALE_COLOR),
)
