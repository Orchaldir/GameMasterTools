package at.orchaldir.gm.core.model.character.appearance.tail

import at.orchaldir.gm.core.model.util.Fill
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TailsLayout {
    None,
    Simple,
}

@Serializable
sealed class Tails {

    fun getType() = when (this) {
        NoTails -> TailsLayout.None
        is SimpleTail -> TailsLayout.Simple
    }

}

@Serializable
@SerialName("None")
data object NoTails : Tails()

@Serializable
@SerialName("Simple")
data class SimpleTail(
    val shape: SimpleTailShape,
    val size: Size,
    val fill: Fill,
) : Tails()
