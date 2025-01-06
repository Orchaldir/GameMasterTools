package at.orchaldir.gm.core.model.item.text.scroll

import at.orchaldir.gm.core.model.material.MaterialId
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
        is ScrollWithOneRod -> handle.calculateLength(rollLength)
        is ScrollWithTwoRods -> handle.calculateLength(rollLength)
    }

    fun calculateWidth(rollDiameter: Distance) = when (this) {
        ScrollWithoutRod -> rollDiameter
        is ScrollWithOneRod -> handle.calculateDiameter(rollDiameter)
        is ScrollWithTwoRods -> handle.calculateDiameter(rollDiameter) * 2
    }

    fun isMadeOf(material: MaterialId) = when (this) {
        is ScrollWithOneRod -> handle.material == material
        is ScrollWithTwoRods -> handle.material == material
        ScrollWithoutRod -> false
    }
}

@Serializable
@SerialName("NoRod")
data object ScrollWithoutRod : ScrollFormat()

@Serializable
@SerialName("OneRod")
data class ScrollWithOneRod(
    val handle: ScrollHandle,
) : ScrollFormat()

@Serializable
@SerialName("TwoRods")
data class ScrollWithTwoRods(
    val handle: ScrollHandle,
) : ScrollFormat()
