package at.orchaldir.gm.core.model.character.appearance.horn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HornsLayout {
    None,
    Two,
    Different,
}

@Serializable
sealed class Horns {

    fun getType() = when (this) {
        NoHorns -> HornsLayout.None
        is TwoHorns -> HornsLayout.Two
        is DifferentHorns -> HornsLayout.Different
    }

}

@Serializable
@SerialName("None")
data object NoHorns : Horns()

@Serializable
@SerialName("Two")
data class TwoHorns(val horn: Horn) : Horns()

@Serializable
@SerialName("Different")
data class DifferentHorns(
    val left: Horn,
    val right: Horn,
) : Horns()


