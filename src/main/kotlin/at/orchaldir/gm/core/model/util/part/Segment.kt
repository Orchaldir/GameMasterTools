package at.orchaldir.gm.core.model.util.part

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable

val MIN_SEGMENT_DISTANCE = Factor.fromPercentage(1)
val MAX_SEGMENT_DISTANCE = Factor.fromPercentage(100)

@Serializable
data class Segment(
    val length: Factor,
    val diameter: Factor,
    val main: ColorItemPart = ColorItemPart(Color.Blue),
    val shape: SegmentShape = SegmentShape.Cylinder,
) : MadeFromParts {
    constructor(length: Factor, diameter: Factor, color: Color, shape: SegmentShape = SegmentShape.Cylinder) :
            this(length, diameter, ColorItemPart(color), shape)

    fun calculateLength(baseLength: Distance) = baseLength * length
    fun calculateDiameter(baseDiameter: Distance) = baseDiameter * diameter

    override fun parts() = listOf(main)
}