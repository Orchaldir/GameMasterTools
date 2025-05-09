package at.orchaldir.gm.core.model.item.text.scroll

import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.item.text.scroll.HandleSegmentShape.Cylinder
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable

@Serializable
data class HandleSegment(
    val length: Distance,
    val diameter: Distance,
    val main: ColorItemPart = ColorItemPart(Color.Blue),
    val shape: HandleSegmentShape = Cylinder,
) : MadeFromParts {
    constructor(length: Distance, diameter: Distance, color: Color, shape: HandleSegmentShape = Cylinder) :
            this(length, diameter, ColorItemPart(color), shape)

    fun calculateSize() = Size2d(diameter, length)

    override fun parts() = listOf(main)
}