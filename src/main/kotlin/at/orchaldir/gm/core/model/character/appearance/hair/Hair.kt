package at.orchaldir.gm.core.model.character.appearance.hair

import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Hair

@Serializable
@SerialName("None")
data object NoHair : Hair()

@Serializable
@SerialName("Normal")
data class NormalHair(
    val style: HairStyle,
    val color: Color,
) : Hair()

