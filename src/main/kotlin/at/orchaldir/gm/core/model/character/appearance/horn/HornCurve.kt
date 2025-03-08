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
data object StraightHorn : HornCurve()

@Serializable
@SerialName("Constant")
data class ConstantCurvature(
    val change: Orientation,
) : HornCurve()

