package at.orchaldir.gm.core.model.character.appearance.beard

import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BeardType {
    None,
    Normal,
}

@Serializable
sealed class Beard {

    fun getType() = when (this) {
        NoBeard -> BeardType.None
        is NormalBeard -> BeardType.Normal
    }

}

@Serializable
@SerialName("None")
data object NoBeard : Beard()

@Serializable
@SerialName("Normal")
data class NormalBeard(
    val style: BeardStyle,
    val color: Color,
) : Beard()

