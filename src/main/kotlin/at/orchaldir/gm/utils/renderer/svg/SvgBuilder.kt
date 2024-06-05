package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.*
import java.util.*

val LOCALE = Locale.US

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

    override fun renderLine(line: List<Point2d>, options: LineOptions) {
        renderPath(convertLineToPath(line), toSvg(options))
    }

    override fun renderPointedOval(center: Point2d, radiusX: Distance, radiusY: Distance, options: RenderOptions) {
        renderPath(convertPointedOvalToPath(center, radiusX, radiusY), toSvg(options))
    }

    override fun renderPolygon(polygon: Polygon2d, options: RenderOptions) {
        renderPath(convertPolygonToPath(polygon), toSvg(options))
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

    override fun renderText(text: String, center: Point2d, options: TextOptions) {
        lines.add(
            String.format(
                LOCALE,
                "  <text x=\"%.3f\" y=\"%.3f\" fill=\"%s\" font-size=\"%.3fpx\" text-anchor=\"middle\">%s</text>",
                center.x,
                center.y,
                toSvg(options.color),
                options.size,
                text,
            )
        )
    }

    private fun renderPath(path: String, style: String) {
        lines.add(
            String.format(
                "  <path d=\"%s\" style=\"%s\"/>",
                path,
                style,
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

fun toSvg(color: RenderColor) = color.toCode()
