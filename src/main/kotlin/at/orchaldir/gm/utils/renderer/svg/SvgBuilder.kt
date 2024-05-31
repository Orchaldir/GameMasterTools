package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.*

class SvgBuilder private constructor(private var lines: MutableList<String> = mutableListOf()) : Renderer {

    companion object {
        fun create(size: Size2d): SvgBuilder {
            val start = String.format(
                "<svg viewBox=\"0 0 %.3f %.3f\" xmlns=\"http://www.w3.org/2000/svg\">",
                size.width,
                size.height
            )
            return SvgBuilder(mutableListOf(start))
        }
    }

    fun finish(): Svg {
        lines.add("</svg>")
        return Svg(lines)
    }

    override fun renderCircle(center: Point2d, radius: Distance, options: RenderOptions) {
        lines.add(
            String.format(
                "  <circle cx=\"%.3f\" cy=\"%.3f\" r=\"%.3f\" style=\"%s\"/>",
                center.x,
                center.y,
                radius.value,
                toSvg(options),
            )
        )
    }

    override fun renderRectangle(aabb: AABB, options: RenderOptions) {
        lines.add(
            String.format(
                "  <rect x=\"%.3f\" y=\"%.3f\" width=\"%.3f\" height=\"%.3f\" style=\"%s\"/>",
                aabb.start.x,
                aabb.start.y,
                aabb.size.width,
                aabb.size.height,
                toSvg(options),
            )
        )
    }

}

fun toSvg(options: RenderOptions): String {
    return when (options) {
        is FillAndBorder -> String.format(
            "fill:%s;%s",
            toSvg(options.fill),
            toSvg(options.border)
        )

        is BorderOnly -> String.format("fill:none;%s", toSvg(options.border))
        is NoBorder -> String.format("fill:%s", toSvg(options.fill))
    }
}

fun toSvg(line: LineOptions): String {
    return String.format("stroke:%s;stroke-width:%.3f", toSvg(line.color), line.width)
}

fun toSvg(color: RenderColor): String {
    return when (color) {
        is NamedColor -> color.color.lowercase()
        is RGB -> color.toHexCode()
    }
}
