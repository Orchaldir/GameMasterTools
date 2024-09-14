package at.orchaldir.gm.core.model.character.appearance.beard

import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Beard

@Serializable
@SerialName("None")
data object NoBeard : Beard()

@Serializable
@SerialName("Normal")
data class NormalBeard(
    val style: BeardStyle,
    val color: Color,
) : Beard()

