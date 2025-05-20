package at.orchaldir.gm.visualization

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.zero
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder

fun visualizeString(
    string: String,
    font: Font,
    fontSize: Distance,
): Svg {
    val size = Size2d(fontSize * string.length, fontSize)
    val aabb = AABB(size)
    val builder = SvgBuilder(size)
    val option = RenderStringOptions(Color.Black.toRender(), fontSize, font)

    builder.getLayer().renderString(string, aabb.getCenter(), zero(), option)

    return builder.finish()
}

