package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.utils.math.Orientation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HornCurveType {
    Straight,
    ConstantCurvature,
}

@Serializable
sealed class HornCurve {

    fun getType() = when (this) {
        is StraightHorn -> HornCurveType.Straight
        is ConstantCurvature -> HornCurveType.ConstantCurvature
    }

}

@Serializable
@SerialName("Straight")
data class StraightHorn(
    val orientation: Orientation,
) : HornCurve()

@Serializable
@SerialName("Constant")
data class ConstantCurvature(
    val start: Orientation,
    val change: Orientation,
) : HornCurve()


