package at.orchaldir.gm.core.model.ecology.plant.appearance

import at.orchaldir.gm.utils.math.Variance
import at.orchaldir.gm.utils.math.unit.Orientation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

    fun calculate(orientation: Orientation, segments: Int) = when (this) {
        is StraightStem -> orientation
        is CurvedStem -> orientation + angle.center / (segments - 1)
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

    constructor(angle: Orientation): this(Variance(angle))

}
