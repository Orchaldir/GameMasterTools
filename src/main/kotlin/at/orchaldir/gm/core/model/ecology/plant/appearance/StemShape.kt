package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Orientation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class StemShapeType {
    Straight,
    Curved,
    Spiral,
}

@Serializable
sealed class StemShape {

    fun getType() = when (this) {
        is StraightStem -> StemShapeType.Straight
        is CurvedStem -> StemShapeType.Curved
        is SpiralStem -> StemShapeType.Spiral
    }

}

@Serializable
@SerialName("Straight")
data object StraightStem : StemShape()

@Serializable
@SerialName("Curved")
data class CurvedStem(
    val change: Orientation,
) : StemShape()

@Serializable
@SerialName("Spiral")
data class SpiralStem(
    val amplitude: Factor,
) : StemShape() {

    init {
        amplitude.requireGreaterZero("Amplitude must be positive!")
    }

}
