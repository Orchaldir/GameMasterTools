package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.*

class SvgBuilder private constructor(private var lines: MutableList<String> = mutableListOf()) : Renderer {

    companion object {
        fun create(size: Size2d): SvgBuilder {
            val start = String.format(
                "<svg viewBox=\"0 0 %d %d\" xmlns=\"http://www.w3.org/2000/svg\">",
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

    override fun renderCircle(center: Point2d, radius: UInt, options: RenderOptions) {
        lines.add(
            String.format(
                "  <circle cx=\"%d\" cy=\"%d\" r=\"%d\" style=\"%s\"/>",
                center.x,
                center.y,
                radius.toInt(),
                toSvg(options),
            )
        )
    }

    override fun renderRectangle(aabb: AABB, options: RenderOptions) {
        lines.add(
            String.format(
                "  <rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" style=\"%s\"/>",
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
            "fill:%s;stroke:%s;stroke-width:%d",
            toSvg(options.fill),
            toSvg(options.border),
            options.lineWidth.toInt()
        )

        is BorderOnly -> String.format("stroke:%s;stroke-width:%d", toSvg(options.border), options.lineWidth)
        is NoBorder -> String.format("fill:%s", toSvg(options.fill))
    }
}

fun toSvg(color: RenderColor): String {
    return when (color) {
        is NamedColor -> color.color.lowercase()
        is RGB -> color.toHexCode()
    }
}

