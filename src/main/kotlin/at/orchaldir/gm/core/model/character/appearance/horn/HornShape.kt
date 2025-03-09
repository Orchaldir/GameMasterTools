package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Orientation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
) : HornShape()
