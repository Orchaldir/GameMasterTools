package at.orchaldir.gm.core.model.item.text.scroll

import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.item.text.scroll.HandleSegmentShape.Cylinder
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.checkDistance
import kotlinx.serialization.Serializable

val MIN_SEGMENT_DISTANCE = Distance.fromCentimeters(1)
val MAX_SEGMENT_DISTANCE = Distance.fromCentimeters(200)

@Serializable
data class HandleSegment(
    val length: Distance,
    val diameter: Distance,
    val main: ColorItemPart = ColorItemPart(Color.Blue),
    val shape: HandleSegmentShape = Cylinder,
) : MadeFromParts {
    constructor(length: Distance, diameter: Distance, color: Color, shape: HandleSegmentShape = Cylinder) :
            this(length, diameter, ColorItemPart(color), shape)

    init {
        checkDistance(length, "length", MIN_SEGMENT_DISTANCE, MAX_SEGMENT_DISTANCE)
        checkDistance(diameter, "diameter", MIN_SEGMENT_DISTANCE, MAX_SEGMENT_DISTANCE)
    }

    fun calculateSize() = Size2d(diameter, length)

    override fun parts() = listOf(main)
}