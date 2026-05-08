package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_CURVE_CENTER = -QUARTER_CIRCLE
val MAX_CURVE_CENTER = QUARTER_CIRCLE
val MAX_CURVE_OFFSET = QUARTER_CIRCLE

enum class StemShapeType {
    Straight,
    Curved,
}

@Serializable
sealed class StemShape {

    fun getType() = when (this) {
        is StraightStem -> StemShapeType.Straight
        is CurvedStem -> StemShapeType.Curved
    }

    fun calculate(
        numberGenerator: NumberGenerator,
        orientation: Orientation,
        segments: Int,
    ) = when (this) {
        is StraightStem -> orientation
        is CurvedStem -> orientation + angle.generate(numberGenerator) / (segments - 1)
    }

    fun validate(label: String) = when (this) {
        is StraightStem -> doNothing()
        is CurvedStem -> angle.validate(
            "$label's curve",
            MIN_CURVE_CENTER,
            MAX_CURVE_CENTER,
            MAX_CURVE_OFFSET,
        )
    }

}

@Serializable
@SerialName("Straight")
data object StraightStem : StemShape()

@Serializable
@SerialName("Curved")
data class CurvedStem(
    /**
     * The angle between the firs & last segment of the stem.
     */
    val angle: Variance<Orientation>,
) : StemShape() {

    constructor(angle: Orientation) : this(Variance(angle))

}
