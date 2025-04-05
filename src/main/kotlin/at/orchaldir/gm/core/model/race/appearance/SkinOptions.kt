package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.SkinColor
import at.orchaldir.gm.core.model.character.appearance.SkinType
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.Serializable

@Serializable
data class SkinOptions(
    val skinTypes: OneOf<SkinType> = OneOf(SkinType.entries),
    val furColors: OneOf<Color> = OneOf(Color.entries),
    val scalesColors: OneOf<Color> = OneOf(Color.entries),
    val normalSkinColors: OneOf<SkinColor> = OneOf(SkinColor.entries),
    val exoticSkinColors: OneOf<Color> = OneOf(Color.entries),
)
