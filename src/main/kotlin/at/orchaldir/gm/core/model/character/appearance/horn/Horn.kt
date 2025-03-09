package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Orientation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HornType {
    Simple,
    Complex,
}

@Serializable
sealed class Horn {

    fun getType() = when (this) {
        is SimpleHorn -> HornType.Simple
        is ComplexHorn -> HornType.Complex
    }

    fun calculatePadding(headHeight: Distance) = when (this) {
        is SimpleHorn -> headHeight * length
        is ComplexHorn -> headHeight * length
    }

}

@Serializable
@SerialName("Simple")
data class SimpleHorn(
    val length: Factor,
    val type: SimpleHornType,
    val color: Color,
) : Horn()

@Serializable
@SerialName("Complex")
data class ComplexHorn(
    val length: Factor,
    val width: Factor,
    val position: HornPosition,
    val orientationOffset: Orientation,
    val shape: HornShape,
    val color: Color,
) : Horn()
