package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Orientation
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.unit.Distance
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
            "cx=\"%.3f\" cy=\"%.3f\" r=\"%.4f\" style=\"%s\"",
            center.x,
            center.y,
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

    override fun renderDiamond(aabb: AABB, options: RenderOptions): LayerRenderer {
        renderPath(convertDiamondToPath(aabb), toSvg(options))

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
            "cx=\"%.3f\" cy=\"%.3f\" rx=\"%.4f\" ry=\"%.4f\"%s style=\"%s\"",
            center.x,
            center.y,
            radiusX.toMeters(),
            radiusY.toMeters(),
            if (orientation != null) {
                formatAttributes(" transform=\"%s\"", rotateAroundCenter(center, orientation))
            } else {
                ""
            },
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
            "x=\"%.3f\" y=\"%.3f\" width=\"%.4f\" height=\"%.4f\" style=\"%s\"",
            aabb.start.x,
            aabb.start.y,
            aabb.size.width,
            aabb.size.height,
            toSvg(options),
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
            "x=\"%.3f\" y=\"%.3f\" alignment-baseline=\"%s\" transform=\"%s\" style=\"%s\" text-anchor=\"middle\"",
            position.x,
            position.y,
            toSvg(options.verticalAlignment),
            rotateAroundCenter(position, orientation),
            toSvg(options),
            text,
        )

        return this
    }

    override fun renderTeardrop(aabb: AABB, options: RenderOptions): LayerRenderer {
        renderPath(convertTeardropToPath(aabb), toSvg(options))

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

    private fun renderPath(path: String, style: String) {
        selfClosingTag("path", "d=\"%s\" style=\"%s\"", path, style)
    }

    private fun rotateAroundCenter(center: Point2d, orientation: Orientation) =
        formatAttributes(
            "rotate(%.3f,%.3f,%.3f)",
            orientation.toDegree(),
            center.x,
            center.y,
        )

    private fun toSvg(verticalAlignment: VerticalAlignment): String {
        return when (verticalAlignment) {
            VerticalAlignment.Top -> "hanging"
            VerticalAlignment.Center -> "middle"
            VerticalAlignment.Bottom -> "baseline"
        }
    }

    private fun toSvg(options: RenderStringOptions) =
        formatAttributes(
            "%s%s;font-size:%.3fpx",
            toSvg(options.renderOptions),
            toSvg(options.font),
            options.size,
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
