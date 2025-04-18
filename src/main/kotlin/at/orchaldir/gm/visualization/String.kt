package at.orchaldir.gm.visualization

import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Orientation.Companion.zero
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder

fun visualizeString(
    string: String,
    font: Font,
    fontSize: Float,
): Svg {
    val size = Size2d(string.length * fontSize, fontSize)
    val aabb = AABB(size)
    val builder = SvgBuilder(size)
    val option = RenderStringOptions(Color.Black.toRender(), fontSize, font)

    builder.getLayer().renderString(string, aabb.getCenter(), zero(), option)

    return builder.finish()
}

fun Boolean.toInt() = when (this) {
    true -> 1
    false -> 0
}
