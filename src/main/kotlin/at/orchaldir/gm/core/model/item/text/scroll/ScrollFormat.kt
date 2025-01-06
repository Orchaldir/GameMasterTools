package at.orchaldir.gm.core.model.item.text.scroll

import at.orchaldir.gm.utils.math.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ScrollFormatType {
    NoRod,
    OneRod,
    TwoRods,
}

@Serializable
sealed class ScrollFormat {

    fun getType() = when (this) {
        is NoRod -> ScrollFormatType.NoRod
        is OneRod -> ScrollFormatType.OneRod
        is TwoRods -> ScrollFormatType.TwoRods
    }

    fun calculateLength(rollLength: Distance) = when (this) {
        NoRod -> rollLength
        is OneRod -> rod.calculateLength(rollLength)
        is TwoRods -> rod.calculateLength(rollLength)
    }

    fun calculateWidth(rollDiameter: Distance) = when (this) {
        NoRod -> rollDiameter
        is OneRod -> rod.calculateDiameter(rollDiameter)
        is TwoRods -> rod.calculateDiameter(rollDiameter) * 2
    }
}

@Serializable
@SerialName("NoRod")
data object NoRod : ScrollFormat()

@Serializable
@SerialName("OneRod")
data class OneRod(
    val rod: ScrollRod,
) : ScrollFormat()

@Serializable
@SerialName("TwoRods")
data class TwoRods(
    val rod: ScrollRod,
) : ScrollFormat()
