package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.core.model.appearance.Fill
import at.orchaldir.gm.core.model.appearance.Solid
import at.orchaldir.gm.core.model.appearance.VerticalStripes
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.*
import java.util.*

val LOCALE: Locale = Locale.US

class SvgBuilder(private val size: Size2d) : Renderer {
    private val patterns: MutableMap<Fill<RenderColor>, String> = mutableMapOf()
    private val layers: MutableMap<Int, MutableList<String>> = mutableMapOf()

    fun finish(): Svg {
        val lines: MutableList<String> = mutableListOf()
        lines.add(getStartLine())

        if (patterns.isNotEmpty()) {
            lines.add("  <defs>")

            patterns.forEach { (fill, name) -> addPatternLines(lines, fill, name) }

            lines.add("  </defs>")
        }

        layers.toSortedMap()
            .values.forEach { layer -> lines.addAll(layer) }

        lines.add("</svg>")
        return Svg(lines)
    }

    override fun renderCircle(center: Point2d, radius: Distance, options: RenderOptions, layer: Int) {
        layers.computeIfAbsent(layer) {
            mutableListOf()
        }.add(
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

    override fun renderCircleArc(
        center: Point2d,
        radius: Distance,
        offset: Orientation,
        angle: Orientation,
        options: RenderOptions,
        layer: Int,
    ) {
        renderPath(convertCircleArcToPath(center, radius, offset, angle), toSvg(options), layer)
    }

    override fun renderEllipse(
        center: Point2d,
        radiusX: Distance,
        radiusY: Distance,
        options: RenderOptions,
        layer: Int,
    ) {
        layers.computeIfAbsent(layer) {
            mutableListOf()
        }.add(
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

    override fun renderLine(line: List<Point2d>, options: LineOptions, layer: Int) {
        renderPath(convertLineToPath(line), toSvg(options), layer)
    }

    override fun renderPointedOval(
        center: Point2d,
        radiusX: Distance,
        radiusY: Distance,
        options: RenderOptions,
        layer: Int,
    ) {
        renderPath(convertPointedOvalToPath(center, radiusX, radiusY), toSvg(options), layer)
    }

    override fun renderRoundedPolygon(polygon: Polygon2d, options: RenderOptions, layer: Int) {
        renderPath(convertRoundedPolygonToPath(polygon), toSvg(options), layer)
    }

    override fun renderPolygon(polygon: Polygon2d, options: RenderOptions, layer: Int) {
        renderPath(convertPolygonToPath(polygon), toSvg(options), layer)
    }

    override fun renderRectangle(aabb: AABB, options: RenderOptions, layer: Int) {
        layers.computeIfAbsent(layer) {
            mutableListOf()
        }.add(
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

    override fun renderText(text: String, center: Point2d, orientation: Orientation, options: TextOptions, layer: Int) {
        layers.computeIfAbsent(layer) {
            mutableListOf()
        }.add(
            String.format(
                LOCALE,
                "  <text x=\"%.3f\" y=\"%.3f\" transform=\"rotate(%.3f,%.3f,%.3f)\" fill=\"%s\" font-size=\"%.3fpx\" text-anchor=\"middle\">%s</text>",
                center.x,
                center.y,
                orientation.toDegree(),
                center.x,
                center.y,
                toSvg(options.color),
                options.size,
                text,
            )
        )
    }

    private fun getStartLine() = String.format(
        LOCALE,
        "<svg viewBox=\"0 0 %.3f %.3f\" xmlns=\"http://www.w3.org/2000/svg\">",
        size.width,
        size.height
    )

    private fun addPatternLines(lines: MutableList<String>, fill: Fill<RenderColor>, name: String) {
        when (fill) {
            is Solid -> error("Solid is not a pattern!")
            is VerticalStripes -> {
                lines.add("    <pattern id=\"$name\" width=\"10%\" height=\"10%\" patternUnits=\"userSpaceOnUse\">")
                lines.add("      <circle  cx=\"0.05\" cy=\"0.05\" r=\"0.05\" fill=\"${toSvg(fill.color0)}\">")
                lines.add("    </pattern>")
            }
        }
    }

    private fun renderPath(path: String, style: String, layer: Int) {
        layers.computeIfAbsent(layer) {
            mutableListOf()
        }.add(
            String.format(
                "  <path d=\"%s\" style=\"%s\"/>",
                path,
                style,
            )
        )
    }

    private fun toSvg(options: RenderOptions): String {
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

    private fun toSvg(line: LineOptions): String {
        return String.format(
            LOCALE, "stroke:%s;stroke-width:%.3f", toSvg(line.color), line.width.value
        )
    }

    private fun toSvg(fill: Fill<RenderColor>) = when (fill) {
        is Solid -> toSvg(fill.color)
        is VerticalStripes -> {
            val name = patterns.computeIfAbsent(fill) { "pattern_${patterns.size}" }
            "url(#$name)"
        }
    }

    private fun toSvg(color: RenderColor) = color.toCode()

}
