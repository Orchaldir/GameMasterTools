package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.*
import java.util.*

val LOCALE: Locale = Locale.US

class SvgRenderer(
    private val patterns: MutableMap<RenderFill, String>,
    private val lines: MutableList<String>,
) : LayerRenderer {

    // LayerRenderer

    override fun renderCircle(center: Point2d, radius: Distance, options: RenderOptions): LayerRenderer {
        selfClosingTag(
            "circle",
            "cx=\"%.3f\" cy=\"%.3f\" r=\"%.3f\" style=\"%s\"",
            center.x,
            center.y,
            radius.value,
            toSvg(options),
        )

        return this
    }

    override fun renderCircleArc(
        center: Point2d,
        radius: Distance,
        offset: Orientation,
        angle: Orientation,
        options: RenderOptions,
    ): LayerRenderer {
        renderPath(convertCircleArcToPath(center, radius, offset, angle), toSvg(options))

        return this
    }

    override fun renderEllipse(
        center: Point2d,
        radiusX: Distance,
        radiusY: Distance,
        options: RenderOptions,
    ): LayerRenderer {
        selfClosingTag(
            "ellipse",
            "cx=\"%.3f\" cy=\"%.3f\" rx=\"%.3f\" ry=\"%.3f\" style=\"%s\"",
            center.x,
            center.y,
            radiusX.value,
            radiusY.value,
            toSvg(options),
        )

        return this
    }

    override fun renderLine(line: List<Point2d>, options: LineOptions): LayerRenderer {
        renderPath(convertLineToPath(line), toSvg(options))

        return this
    }

    override fun renderPointedOval(
        center: Point2d,
        radiusX: Distance,
        radiusY: Distance,
        options: RenderOptions,
    ): LayerRenderer {
        renderPath(convertPointedOvalToPath(center, radiusX, radiusY), toSvg(options))

        return this
    }

    override fun renderRoundedPolygon(polygon: Polygon2d, options: RenderOptions): LayerRenderer {
        renderPath(convertRoundedPolygonToPath(polygon), toSvg(options))

        return this
    }

    override fun renderPolygon(polygon: Polygon2d, options: RenderOptions): LayerRenderer {
        renderPath(convertPolygonToPath(polygon), toSvg(options))

        return this
    }

    override fun renderRectangle(aabb: AABB, options: RenderOptions): LayerRenderer {
        selfClosingTag(
            "rect",
            "x=\"%.3f\" y=\"%.3f\" width=\"%.3f\" height=\"%.3f\" style=\"%s\"",
            aabb.start.x,
            aabb.start.y,
            aabb.size.width,
            aabb.size.height,
            toSvg(options),
        )

        return this
    }

    override fun renderText(
        text: String,
        center: Point2d,
        orientation: Orientation,
        options: TextOptions,
    ): LayerRenderer {
        inlineTag(
            "text",
            text,
            "x=\"%.3f\" y=\"%.3f\" transform=\"rotate(%.3f,%.3f,%.3f)\" fill=\"%s\" font-size=\"%.3fpx\" text-anchor=\"middle\"",
            center.x,
            center.y,
            orientation.toDegree(),
            center.x,
            center.y,
            toSvg(options.color),
            options.size,
            text,
        )

        return this
    }

    //

    fun tag(tag: String, format: String, vararg args: Any?, content: (LayerRenderer) -> Unit) {
        val attributes = String.format(
            LOCALE,
            format,
            *args,
        )
        lines.add(String.format("  <%s %s>", tag, attributes))

        content(this)

        lines.add(String.format("  </%s>", tag))
    }

    private fun inlineTag(tag: String, text: String, format: String, vararg args: Any?) {
        val attributes = String.format(
            LOCALE,
            format,
            *args,
        )
        lines.add(String.format("  <%s %s>%s</%s>", tag, attributes, text, tag))
    }

    private fun selfClosingTag(tag: String, format: String, vararg args: Any?) {
        val attributes = String.format(
            LOCALE,
            format,
            *args,
        )
        lines.add(String.format("  <%s %s/>", tag, attributes))
    }

    private fun renderPath(path: String, style: String) {
        selfClosingTag("path", "d=\"%s\" style=\"%s\"", path, style)
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
