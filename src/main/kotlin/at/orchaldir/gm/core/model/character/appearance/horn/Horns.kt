package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.ZERO
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

    fun calculatePadding(headHeight: Distance) = when (this) {
        NoHorns -> Distance(0)
        is TwoHorns -> horn.calculatePadding(headHeight)
        is DifferentHorns -> left.calculatePadding(headHeight).max(right.calculatePadding(headHeight))
    } * 2.0f

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


