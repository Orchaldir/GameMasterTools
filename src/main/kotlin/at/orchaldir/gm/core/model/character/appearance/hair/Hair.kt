package at.orchaldir.gm.core.model.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.EyesLayout
import at.orchaldir.gm.core.model.character.appearance.NoEyes
import at.orchaldir.gm.core.model.character.appearance.OneEye
import at.orchaldir.gm.core.model.character.appearance.TwoEyes
import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HairType {
    None,
    Normal,
}

@Serializable
sealed class Hair {

    fun getType() = when (this) {
        NoHair -> HairType.None
        is NormalHair -> HairType.Normal
    }

}

@Serializable
@SerialName("None")
data object NoHair : Hair()

@Serializable
@SerialName("Normal")
data class NormalHair(
    val style: HairStyle,
    val color: Color,
) : Hair()

