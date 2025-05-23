package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.FeatureColor
import at.orchaldir.gm.core.model.character.appearance.ReuseSkinColor
import at.orchaldir.gm.core.model.util.render.Color
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
    val color: FeatureColor = ReuseSkinColor,
) : Horns() {

    init {
        require(front in VALID_CROWN_HORNS) { "Invalid number of horns in the front!" }
        require(back in VALID_CROWN_HORNS) { "Invalid number of horns in the back!" }
        length.requireGreaterZero("Length must be positive!")
        width.requireGreaterZero("Width must be positive!")
    }
}


