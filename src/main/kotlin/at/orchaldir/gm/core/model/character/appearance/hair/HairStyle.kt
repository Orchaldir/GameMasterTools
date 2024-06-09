package at.orchaldir.gm.core.model.character.appearance.hair

import at.orchaldir.gm.core.model.appearance.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class HairStyle

@Serializable
@SerialName("Short")
data class ShortHair(
    val style: ShortHairStyle,
    val color: Color = Color.Yellow,
) : HairStyle()