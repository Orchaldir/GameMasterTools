package at.orchaldir.gm.core.model.item.text.scroll

import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.part.Segments
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ScrollFormatType {
    NoRod,
    OneRod,
    TwoRods,
}

@Serializable
sealed class ScrollFormat : MadeFromParts {

    fun getType() = when (this) {
        is ScrollWithoutRod -> ScrollFormatType.NoRod
        is ScrollWithOneRod -> ScrollFormatType.OneRod
        is ScrollWithTwoRods -> ScrollFormatType.TwoRods
    }

    fun calculateLength(rollLength: Distance) = when (this) {
        ScrollWithoutRod -> rollLength
        is ScrollWithOneRod -> calculateLengthWithSegments(rollLength, handle)
        is ScrollWithTwoRods -> calculateLengthWithSegments(rollLength, handle)
    }

    private fun calculateLengthWithSegments(rollLength: Distance, segments: Segments) =
        rollLength + segments.calculateLength(rollLength) * 2

    fun calculateWidth(rollDiameter: Distance) = when (this) {
        ScrollWithoutRod -> rollDiameter
        is ScrollWithOneRod -> calculateDiameter(rollDiameter, handle)
        is ScrollWithTwoRods -> calculateDiameter(rollDiameter, handle) * 2
    }

    fun calculateWidthOfOneRod(rollDiameter: Distance) = when (this) {
        ScrollWithoutRod -> rollDiameter
        is ScrollWithOneRod -> calculateDiameter(rollDiameter, handle)
        is ScrollWithTwoRods -> calculateDiameter(rollDiameter, handle)
    }

    private fun calculateDiameter(rollDiameter: Distance, segments: Segments) =
        rollDiameter.max(segments.calculateDiameter(rollDiameter))
}

@Serializable
@SerialName("NoRod")
data object ScrollWithoutRod : ScrollFormat()

@Serializable
@SerialName("OneRod")
data class ScrollWithOneRod(
    val handle: Segments,
) : ScrollFormat() {

    override fun parts() = handle.parts()

}

@Serializable
@SerialName("TwoRods")
data class ScrollWithTwoRods(
    val handle: Segments,
) : ScrollFormat() {

    override fun parts() = handle.parts()

}
