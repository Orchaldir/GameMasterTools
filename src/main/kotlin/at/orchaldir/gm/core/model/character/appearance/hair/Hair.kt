package at.orchaldir.gm.core.model.character.appearance.hair

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Hair

@Serializable
@SerialName("None")
data object NoHair : Hair()

@Serializable
@SerialName("Fire")
data class FireHair(
    val size: Size,
) : Hair()

@Serializable
@SerialName("Normal")
data class NormalHair(
    val style: HairStyle,
) : Hair()

