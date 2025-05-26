package at.orchaldir.gm.core.model.util.part

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable

val MIN_SEGMENT_DISTANCE = Distance.fromCentimeters(1)
val MAX_SEGMENT_DISTANCE = Distance.fromCentimeters(200)

@Serializable
data class Segment(
    val length: Distance,
    val diameter: Distance,
    val main: ColorItemPart = ColorItemPart(Color.Blue),
    val shape: SegmentShape = SegmentShape.Cylinder,
) : MadeFromParts {
    constructor(length: Distance, diameter: Distance, color: Color, shape: SegmentShape = SegmentShape.Cylinder) :
            this(length, diameter, ColorItemPart(color), shape)

    fun calculateSize() = Size2d(diameter, length)

    override fun parts() = listOf(main)
}