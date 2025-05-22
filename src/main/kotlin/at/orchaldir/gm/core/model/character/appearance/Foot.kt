package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FootType {
    Clawed,
    Normal,
}

@Serializable
sealed class Foot {

    fun getType() = when (this) {
        is ClawedFoot -> FootType.Clawed
        NormalFoot -> FootType.Normal
    }

}

@Serializable
@SerialName("Clawed")
data class ClawedFoot(
    val count: Int = 4,
    val size: Size = Size.Medium,
    val color: Color = Color.Black,
) : Foot()


@Serializable
@SerialName("Normal")
data object NormalFoot : Foot()

