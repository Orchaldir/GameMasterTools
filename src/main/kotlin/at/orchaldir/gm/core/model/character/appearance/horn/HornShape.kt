package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Orientation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MIN_SPIRAL_CYCLES = 2
const val MAX_SPIRAL_CYCLES = 10

enum class HornShapeType {
    Straight,
    Curved,
    Spiral,
}

@Serializable
sealed class HornShape {

    fun getType() = when (this) {
        is StraightHorn -> HornShapeType.Straight
        is CurvedHorn -> HornShapeType.Curved
        is SpiralHorn -> HornShapeType.Spiral
    }

}

@Serializable
@SerialName("Straight")
data object StraightHorn : HornShape()

@Serializable
@SerialName("Curved")
data class CurvedHorn(
    val change: Orientation,
) : HornShape()

@Serializable
@SerialName("Spiral")
data class SpiralHorn(
    val cycles: Int,
    val amplitude: Factor,
) : HornShape() {

    init {
        require(cycles in MIN_SPIRAL_CYCLES..MAX_SPIRAL_CYCLES) { "Invalid number of cycles!" }
        require(amplitude.value > 0.0f) { "Amplitude must be positive!" }
    }

}
