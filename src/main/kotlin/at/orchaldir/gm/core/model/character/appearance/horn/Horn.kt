package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.core.model.character.appearance.FeatureColor
import at.orchaldir.gm.core.model.character.appearance.ReuseSkinColor
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.Distance
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
        is SimpleHorn -> headHeight * length * when (simpleType) {
            SimpleHornType.Mouflon -> 0.16f
            SimpleHornType.WaterBuffalo -> 0.2f
            else -> 1.0f
        }

        is ComplexHorn -> headHeight * length
    }

}

@Serializable
@SerialName("Simple")
data class SimpleHorn(
    val length: Factor,
    val simpleType: SimpleHornType,
    val color: FeatureColor = ReuseSkinColor,
) : Horn() {

    init {
        length.requireGreaterZero("Length must be positive!")
    }
}

@Serializable
@SerialName("Complex")
data class ComplexHorn(
    val length: Factor,
    val relativeWidth: Factor,
    val position: HornPosition,
    val orientationOffset: Orientation,
    val shape: HornShape,
    val color: FeatureColor = ReuseSkinColor,
) : Horn() {

    init {
        length.requireGreaterZero("Length must be positive!")
        relativeWidth.requireGreaterZero("Relative width must be positive!")
    }

    fun getWidth() = length * relativeWidth

}
