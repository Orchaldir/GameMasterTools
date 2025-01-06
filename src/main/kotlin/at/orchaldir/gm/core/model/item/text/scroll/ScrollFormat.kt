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
        is ScrollWithoutRod -> ScrollFormatType.NoRod
        is ScrollWithOneRod -> ScrollFormatType.OneRod
        is ScrollWithTwoRods -> ScrollFormatType.TwoRods
    }

    fun calculateLength(rollLength: Distance) = when (this) {
        ScrollWithoutRod -> rollLength
        is ScrollWithOneRod -> rod.calculateLength(rollLength)
        is ScrollWithTwoRods -> rod.calculateLength(rollLength)
    }

    fun calculateWidth(rollDiameter: Distance) = when (this) {
        ScrollWithoutRod -> rollDiameter
        is ScrollWithOneRod -> rod.calculateDiameter(rollDiameter)
        is ScrollWithTwoRods -> rod.calculateDiameter(rollDiameter) * 2
    }
}

@Serializable
@SerialName("NoRod")
data object ScrollWithoutRod : ScrollFormat()

@Serializable
@SerialName("OneRod")
data class ScrollWithOneRod(
    val rod: ScrollRod,
) : ScrollFormat()

@Serializable
@SerialName("TwoRods")
data class ScrollWithTwoRods(
    val rod: ScrollRod,
) : ScrollFormat()
