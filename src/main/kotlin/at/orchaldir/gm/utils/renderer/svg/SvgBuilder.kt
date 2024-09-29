package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LinkRenderer
import at.orchaldir.gm.utils.renderer.model.*
import java.util.*

val LOCALE: Locale = Locale.US

class SvgBuilder(private val size: Size2d) : LinkRenderer {
    private val patterns: MutableMap<RenderFill, String> = mutableMapOf()
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

    // LinkRenderer

    override fun link(link: String, layer: Int) {
        layers.computeIfAbsent(layer) {
            mutableListOf()
        }.add(String.format(LOCALE, "  <a href=\"%s\" target=\"_parent\">", link))
    }

    override fun closeLink(layer: Int) {
        layers.computeIfAbsent(layer) {
            mutableListOf()
        }.add("  </a>")
    }

    // Renderer

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

    private fun addPatternLines(lines: MutableList<String>, fill: RenderFill, name: String) {
        when (fill) {
            is RenderSolid -> error("Solid is not a pattern!")
            is RenderVerticalStripes -> addStripes(lines, name, fill.color0, fill.color1, fill.width)
            is RenderHorizontalStripes -> addStripes(
                lines,
                name,
                fill.color0,
                fill.color1,
                fill.width,
                " gradientTransform=\"rotate(90)\""
            )
        }
    }

    private fun SvgBuilder.addStripes(
        lines: MutableList<String>,
        name: String,
        color0: RenderColor,
        color1: RenderColor,
        width: UByte,
        options: String = "",
    ) {
        val c0 = toSvg(color0)
        val c1 = toSvg(color1)
        lines.add("    <linearGradient id=\"$name\" spreadMethod=\"repeat\" x2=\"$width%\" gradientUnits=\"userSpaceOnUse\"$options>")
        lines.add("      <stop offset=\"0\" stop-color=\"$c0\"/>>")
        lines.add("      <stop offset=\"0.5\" stop-color=\"$c0\"/>>")
        lines.add("      <stop offset=\"0.5\" stop-color=\"$c1\"/>>")
        lines.add("      <stop offset=\"1.0\" stop-color=\"$c1\"/>>")
        lines.add("    </linearGradient>")
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

    private fun toSvg(fill: RenderFill) = when (fill) {
        is RenderSolid -> toSvg(fill.color)
        else -> {
            val name = patterns.computeIfAbsent(fill) { "pattern_${patterns.size}" }
            "url(#$name)"
        }
    }

    private fun toSvg(color: RenderColor) = color.toCode()

}
