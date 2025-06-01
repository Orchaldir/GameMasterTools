package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.*
import java.util.*

val LOCALE: Locale = Locale.US

class SvgRenderer(
    private val fonts: MutableSet<Font>,
    private val patterns: MutableMap<RenderFill, String>,
    private val lines: MutableList<String>,
    private val indent: String,
    private val step: String,
    private val tooltip: String? = null,
) : LayerRenderer {

    // LayerRenderer

    override fun renderCircle(center: Point2d, radius: Distance, options: RenderOptions): LayerRenderer {
        selfClosingTag(
            "circle",
            "cx=\"%.4f\" cy=\"%.4f\" r=\"%.4f\" style=\"%s\"",
            center.x.toMeters(),
            center.y.toMeters(),
            radius.toMeters(),
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
        orientation: Orientation,
        radiusX: Distance,
        radiusY: Distance,
        options: RenderOptions,
    ) = renderOrientedEllipse(center, orientation, radiusX, radiusY, options)

    override fun renderEllipse(
        center: Point2d,
        radiusX: Distance,
        radiusY: Distance,
        options: RenderOptions,
    ) = renderOrientedEllipse(center, null, radiusX, radiusY, options)

    private fun renderOrientedEllipse(
        center: Point2d,
        orientation: Orientation?,
        radiusX: Distance,
        radiusY: Distance,
        options: RenderOptions,
    ): LayerRenderer {
        selfClosingTag(
            "ellipse",
            "cx=\"%.4f\" cy=\"%.4f\" rx=\"%.4f\" ry=\"%.4f\"%s style=\"%s\"",
            center.x.toMeters(),
            center.y.toMeters(),
            radiusX.toMeters(),
            radiusY.toMeters(),
            toSvg(orientation, center),
            toSvg(options),
        )

        return this
    }

    override fun renderLine(line: List<Point2d>, options: LineOptions): LayerRenderer {
        renderPath(convertLineToPath(line), toSvgWithoutFill(options))

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
        renderPath(convertRoundedPolygonToPath(polygon), toSvg(options), options.clipping())

        return this
    }

    override fun renderPolygon(polygon: Polygon2d, options: RenderOptions): LayerRenderer {
        renderPath(convertPolygonToPath(polygon), toSvg(options), options.clipping())

        return this
    }

    fun renderPolygon(polygon: Polygon2d): LayerRenderer {
        renderPath(convertPolygonToPath(polygon))

        return this
    }

    override fun renderPolygonWithHole(
        polygon: Polygon2d,
        hole: Polygon2d,
        options: RenderOptions,
    ): LayerRenderer {
        renderPath(convertPolygonWithHoleToPath(polygon, hole), toSvg(options))

        return this
    }

    override fun renderRoundedPolygonWithHole(
        polygon: Polygon2d,
        hole: Polygon2d,
        options: RenderOptions,
    ): LayerRenderer {
        renderPath(convertRoundedPolygonWithHoleToPath(polygon, hole), toSvg(options))

        return this
    }

    override fun renderPolygonWithRoundedHole(
        polygon: Polygon2d,
        hole: Polygon2d,
        options: RenderOptions,
    ): LayerRenderer {
        renderPath(convertPolygonWithRoundedHoleToPath(polygon, hole), toSvg(options))

        return this
    }

    override fun renderRoundedPolygonWithRoundedHole(
        polygon: Polygon2d,
        hole: Polygon2d,
        options: RenderOptions,
    ): LayerRenderer {
        renderPath(convertRoundedPolygonWithRoundedHoleToPath(polygon, hole), toSvg(options))

        return this
    }

    override fun renderRectangle(aabb: AABB, options: RenderOptions): LayerRenderer {
        selfClosingTag(
            "rect",
            "x=\"%.4f\" y=\"%.4f\" width=\"%.4f\" height=\"%.4f\" style=\"%s\"%s",
            aabb.start.x.toMeters(),
            aabb.start.y.toMeters(),
            aabb.size.width.toMeters(),
            aabb.size.height.toMeters(),
            toSvg(options),
            toSvgClipPath(options.clipping())
        )

        return this
    }

    override fun renderHollowRectangle(
        center: Point2d,
        width: Distance,
        height: Distance,
        thickness: Distance,
        options: RenderOptions,
    ): LayerRenderer {
        renderPath(convertHollowRectangleToPath(center, width, height, thickness), toSvg(options))

        return this
    }

    override fun renderRing(
        center: Point2d,
        outerRadius: Distance,
        innerRadius: Distance,
        options: RenderOptions,
    ): LayerRenderer {
        renderPath(convertRingToPath(center, outerRadius, innerRadius), toSvg(options))

        return this
    }

    override fun renderString(
        text: String,
        position: Point2d,
        orientation: Orientation,
        options: RenderStringOptions,
    ): LayerRenderer {
        inlineTag(
            "text",
            text,
            "x=\"%.4f\" y=\"%.4f\" alignment-baseline=\"%s\"%s style=\"%s\" text-anchor=\"%s\"",
            position.x.toMeters(),
            position.y.toMeters(),
            toSvg(options.verticalAlignment),
            toSvg(orientation, position),
            toSvg(options),
            toSvg(options.horizontalAlignment),
        )

        return this
    }

    override fun renderString(
        text: String,
        start: Point2d,
        width: Distance,
        options: RenderStringOptions,
    ): LayerRenderer {
        val position = when (options.horizontalAlignment) {
            HorizontalAlignment.Start, HorizontalAlignment.Justified -> start
            HorizontalAlignment.Center -> start.addWidth(width / 2.0f)
            HorizontalAlignment.End -> start.addWidth(width)
        }
        inlineTag(
            "text",
            text,
            "x=\"%.4f\" y=\"%.4f\" alignment-baseline=\"%s\" style=\"%s\" text-anchor=\"%s\"",
            position.x.toMeters(),
            position.y.toMeters(),
            toSvg(options.verticalAlignment),
            toSvg(options),
            toSvg(options.horizontalAlignment),
        )

        return this
    }

    //

    fun font(font: Font) {
        customTag("@font-face{", "}") {
            it.addLine("font-family:\"${font.name}\";")
            it.addLine("src:url(data:application/font-woff;charset=utf-8;base64,${font.base64})")
            it.addLine("format(\"woff\");")
            it.addLine("font-weight:normal;")
            it.addLine("font-style:normal;")
        }
    }

    fun tag(tag: String, attributes: String = "", content: (SvgRenderer) -> Unit) {
        customTag(
            String.format("<%s%s>", tag, attributes),
            String.format("</%s>", tag),
            content,
        )
    }

    fun tag(tag: String, format: String, vararg args: Any?, content: (SvgRenderer) -> Unit) {
        val attributes = formatAttributes(format, *args)
        tag(tag, " $attributes") {
            content(it)
        }
    }

    fun customTag(start: String, end: String, content: (SvgRenderer) -> Unit) {
        addLine(start)

        content(SvgRenderer(fonts, patterns, lines, indent + step, step, tooltip))

        addLine(end)
    }

    private fun inlineTag(tag: String, text: String, format: String, vararg args: Any?) {

        val attributes = formatAttributes(format, *args)
        addLine(String.format("<%s %s>%s</%s>", tag, attributes, text, tag))
    }

    fun selfClosingTag(tag: String, format: String, vararg args: Any?) {
        if (tooltip == null) {
            val attributes = formatAttributes(format, *args)
            addLine(String.format("<%s %s/>", tag, attributes))
        } else {
            tag(tag, format, *args) {
                addLine("<title>$tooltip</title>")
            }
        }
    }

    private fun formatAttributes(format: String, vararg args: Any?) = String.format(
        LOCALE,
        format,
        *args,
    )

    private fun addLine(line: String) {
        lines.add(indent + line)
    }

    private fun renderPath(path: String, style: String, clipping: String? = null) {
        val clippingAttribute = toSvgClipPath(clipping)

        selfClosingTag(
            "path", "d=\"%s\" style=\"%s\"%s",
            path,
            style,
            clippingAttribute,
        )
    }

    private fun toSvgClipPath(clipping: String?): String = if (clipping != null) {
        " clip-path=\"url(#$clipping)\""
    } else {
        ""
    }

    private fun renderPath(path: String) {
        selfClosingTag("path", "d=\"%s\"", path)
    }

    private fun rotateAroundCenter(center: Point2d, orientation: Orientation) =
        formatAttributes(
            "rotate(%.3f,%.3f,%.3f)",
            orientation.toDegrees(),
            center.x.toMeters(),
            center.y.toMeters(),
        )

    private fun toSvg(
        orientation: Orientation?,
        center: Point2d,
    ) = if (orientation != null && !orientation.isZero()) {
        formatAttributes(" transform=\"%s\"", rotateAroundCenter(center, orientation))
    } else {
        ""
    }

    private fun toSvg(verticalAlignment: VerticalAlignment): String {
        return when (verticalAlignment) {
            VerticalAlignment.Top -> "hanging"
            VerticalAlignment.Center -> "middle"
            VerticalAlignment.Bottom -> "baseline"
        }
    }

    private fun toSvg(alignment: HorizontalAlignment): String {
        return when (alignment) {
            HorizontalAlignment.Start, HorizontalAlignment.Justified -> "start"
            HorizontalAlignment.Center -> "middle"
            HorizontalAlignment.End -> "end"
        }
    }

    private fun toSvg(options: RenderStringOptions) =
        formatAttributes(
            "%s%s;font-size:%.3fpx",
            toSvg(options.renderOptions),
            toSvg(options.font),
            options.size.toMeters(),
        )

    private fun toSvg(font: Font?) = if (font != null) {
        fonts.add(font)
        String.format(";font-family:'%s'", font.name)
    } else {
        ""
    }

    private fun toSvg(options: RenderOptions): String {
        return when (options) {
            is FillAndBorder -> String.format(
                "fill:%s;%s",
                toSvg(options.fill),
                toSvg(options.border)
            )

            is BorderOnly -> toSvgWithoutFill(options.border)
            is NoBorder -> String.format("fill:%s", toSvg(options.fill))
        }
    }

    private fun toSvgWithoutFill(line: LineOptions) = "fill:none;" + toSvg(line)

    private fun toSvg(line: LineOptions): String {
        return String.format(
            LOCALE, "stroke:%s;stroke-width:%.4f", toSvg(line.color), line.width.toMeters()
        )
    }

    private fun toSvg(fill: RenderFill) = when (fill) {
        is RenderSolid -> toSvg(fill.color)
        is RenderTransparent -> {
            val color = toSvg(fill.color)
            "$color;fill-opacity:${fill.opacity.toNumber()}"
        }

        else -> {
            val name = patterns.computeIfAbsent(fill) { "pattern_${patterns.size}" }
            "url(#$name)"
        }
    }

    private fun toSvg(color: RenderColor) = color.toCode()
}
