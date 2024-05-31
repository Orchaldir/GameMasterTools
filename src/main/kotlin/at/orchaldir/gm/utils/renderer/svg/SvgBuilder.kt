package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.*
import java.lang.Float.min
import java.util.*
import kotlin.math.pow

private val LOCALE = Locale.US

class SvgBuilder private constructor(private var lines: MutableList<String> = mutableListOf()) : Renderer {

    companion object {
        fun create(size: Size2d): SvgBuilder {
            val start = String.format(
                LOCALE,
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
                LOCALE,
                "  <circle cx=\"%.3f\" cy=\"%.3f\" r=\"%.3f\" style=\"%s\"/>",
                center.x,
                center.y,
                radius.value,
                toSvg(options),
            )
        )
    }

    override fun renderEllipse(center: Point2d, radiusX: Distance, radiusY: Distance, options: RenderOptions) {
        lines.add(
            String.format(
                LOCALE,
                "  <ellipse cx=\"%.3f\" cy=\"%.3f\" rx=\"%.3f\" ry=\"%.3f\" style=\"%s\"/>",
                center.x,
                center.y,
                radiusX.value,
                radiusY.value,
                toSvg(options),
            )
        )
    }

    override fun renderPointedOval(center: Point2d, radiusX: Distance, radiusY: Distance, options: RenderOptions) {
        val radius = (radiusX.value.pow(2.0f) + radiusY.value.pow(2.0f)) / (2.0f * min(radiusX.value, radiusY.value))
        val aabb = AABB.fromRadii(center, radiusX, radiusY)
        val left = if (radiusX.value > radiusY.value) {
            aabb.getPoint(0.0f, 0.5f)
        } else {
            aabb.getPoint(0.5f, 0.0f)
        }
        val right = if (radiusX.value > radiusY.value) {
            aabb.getPoint(1.0f, 0.5f)
        } else {
            aabb.getPoint(0.5f, 1.0f)
        }

        renderPath(
            String.format(
                LOCALE,
                "M %.3f %.3f A %.3f %.3f, 0, 0, 0, %.3f %.3f A %.3f %.3f, 0, 0, 0, %.3f %.3f Z",
                left.x, left.y, radius, radius, right.x, right.y, radius, radius, left.x, left.y,
            ), options
        )
    }

    override fun renderRectangle(aabb: AABB, options: RenderOptions) {
        lines.add(
            String.format(
                LOCALE,
                "  <rect x=\"%.3f\" y=\"%.3f\" width=\"%.3f\" height=\"%.3f\" style=\"%s\"/>",
                aabb.start.x,
                aabb.start.y,
                aabb.size.width,
                aabb.size.height,
                toSvg(options),
            )
        )
    }

    private fun renderPath(path: String, options: RenderOptions) {
        lines.add(
            String.format(
                "  <path  d=\"%s\" style=\"%s\"/>",
                path,
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
    return String.format(
        LOCALE, "stroke:%s;stroke-width:%.3f", toSvg(line.color), line.width.value
    )
}

fun toSvg(color: RenderColor): String {
    return when (color) {
        is NamedColor -> color.color.lowercase()
        is RGB -> color.toHexCode()
    }
}
