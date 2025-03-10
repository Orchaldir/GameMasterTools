package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val DEFAULT_HORN_COLOR = Color.Red
val VALID_CROWN_HORNS = 1..5

enum class HornsLayout {
    None,
    Two,
    Different,
    Crown,
}

@Serializable
sealed class Horns {

    fun getType() = when (this) {
        NoHorns -> HornsLayout.None
        is TwoHorns -> HornsLayout.Two
        is DifferentHorns -> HornsLayout.Different
        is CrownOfHorns -> HornsLayout.Crown
    }

    fun calculatePadding(headHeight: Distance) = when (this) {
        NoHorns -> Distance(0)
        is TwoHorns -> horn.calculatePadding(headHeight)
        is DifferentHorns -> left.calculatePadding(headHeight).max(right.calculatePadding(headHeight))
        is CrownOfHorns -> headHeight * length
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

@Serializable
@SerialName("Crown")
data class CrownOfHorns(
    val front: Int,
    val back: Int,
    val hasSideHorns: Boolean,
    val length: Factor,
    val width: Factor,
    val color: Color,
) : Horns() {

    init {
        require(front in VALID_CROWN_HORNS) { "Invalid number of horns in the front!" }
        require(back in VALID_CROWN_HORNS) { "Invalid number of horns in the back!" }
        require(length.value > 0.0f) { "Length must be positive!" }
        require(width.value > 0.0f) { "Width must be positive!" }
    }
}


