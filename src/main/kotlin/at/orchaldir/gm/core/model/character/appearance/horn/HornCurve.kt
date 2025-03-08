package at.orchaldir.gm.core.model.character.appearance.horn

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Orientation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HornCurveType {
    Straight,
    ConstantCurvature,
    Wave,
}

@Serializable
sealed class HornCurve {

    fun getType() = when (this) {
        is StraightHorn -> HornCurveType.Straight
        is ConstantCurvature -> HornCurveType.ConstantCurvature
        is WaveCurve -> HornCurveType.Wave
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

@Serializable
@SerialName("Wave")
data class WaveCurve(
    val cycles: Int,
    val amplitude: Factor,
) : HornCurve()
