package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.renderer.AdvancedRenderer
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.TransformRenderer
import at.orchaldir.gm.utils.renderer.model.*

private val CLIPPING_PREFIX = "clip_"

class SvgBuilder(private val size: Size2d) : AdvancedRenderer {
    private val fonts: MutableSet<Font> = mutableSetOf()
    private val clippings: MutableMap<String, Polygon2d> = mutableMapOf()
    private val patterns: MutableMap<RenderFill, String> = mutableMapOf()
    private val layers: MutableMap<Int, MutableList<String>> = mutableMapOf()
    private val step: String = "  "

    fun finish(): Svg {
        val lines: MutableList<String> = mutableListOf()
        lines.add(getStartLine())

        if (clippings.isNotEmpty() ||
            fonts.isNotEmpty() ||
            patterns.isNotEmpty()
        ) {
            val patternLines = mutableListOf<String>()
            val renderer = SvgRenderer(fonts, patterns, patternLines, step, step)

            renderer.tag("defs") { tag ->
                clippings.forEach { (name, polygon) ->
                    renderer.tag("clipPath", "id=\"%s\"", name) { clipRenderer ->
                        clipRenderer.renderPolygon(polygon)
                    }
                }

                patterns.forEach { (fill, name) -> addPatternLines(tag, fill, name) }

                renderer.tag("style") { styleRenderer ->
                    fonts.forEach { font ->
                        styleRenderer.font(font)
                    }
                }
            }

            lines.addAll(patternLines)
        }

        layers.toSortedMap()
            .values.forEach { layer -> lines.addAll(layer) }

        lines.add("</svg>")
        return Svg(lines)
    }

    // layers

    override fun getLayer(layer: Int) = SvgRenderer(fonts, patterns, layers.computeIfAbsent(layer) {
        mutableListOf()
    }, step, step)

    // clippings

    override fun createClipping(polygon: Polygon2d): String {
        val name = CLIPPING_PREFIX + clippings.size

        clippings.put(name, polygon)

        return name
    }

    // group

    override fun createGroup(position: Point2d, layerIndex: Int, content: (TransformRenderer) -> Unit) {
        val layer = getLayer(layerIndex)

        layer.createGroup(position) {
            content(it)
        }
    }

    override fun createGroup(orientation: Orientation, layerIndex: Int, content: (TransformRenderer) -> Unit) {
        val layer = getLayer(layerIndex)

        layer.createGroup(orientation) {
            content(it)
        }
    }

    // links

    override fun link(link: String, layerIndex: Int, content: (LayerRenderer) -> Unit) {
        val layer = SvgRenderer(fonts, patterns, layers.computeIfAbsent(layerIndex) { mutableListOf() }, step, step)

        layer.tag("a", "href=\"%s\" target=\"_parent\"", link) {
            content(it)
        }
    }

    override fun tooltip(text: String, layerIndex: Int, content: (LayerRenderer) -> Unit) {
        val layer =
            SvgRenderer(fonts, patterns, layers.computeIfAbsent(layerIndex) { mutableListOf() }, step, step, text)

        content(layer)
    }

    override fun linkAndTooltip(link: String, tooltip: String, layerIndex: Int, content: (LayerRenderer) -> Unit) {
        val layer =
            SvgRenderer(fonts, patterns, layers.computeIfAbsent(layerIndex) { mutableListOf() }, step, step, tooltip)

        layer.tag("a", "href=\"%s\" target=\"_parent\"", link) {
            content(it)
        }
    }

    override fun optionalLinkAndTooltip(
        link: String?,
        tooltip: String?,
        layerIndex: Int,
        content: (LayerRenderer) -> Unit,
    ) {
        if (link != null && tooltip != null) {
            linkAndTooltip(link, tooltip, layerIndex, content)
        } else if (link != null) {
            link(link, layerIndex, content)
        } else if (tooltip != null) {
            tooltip(tooltip, layerIndex, content)
        } else {
            content(getLayer(layerIndex))
        }
    }

    //

    private fun getStartLine() = String.format(
        LOCALE,
        "<svg viewBox=\"0 0 %.3f %.3f\" xmlns=\"http://www.w3.org/2000/svg\">",
        size.width.toMeters(),
        size.height.toMeters(),
    )

    private fun addPatternLines(renderer: SvgRenderer, fill: RenderFill, name: String) {
        when (fill) {
            is RenderCircles -> addCircles(renderer, name, fill)
            is RenderSolid -> error("Solid is not a pattern!")
            is RenderTransparent -> error("Transparent is not a pattern!")
            is RenderVerticalStripes -> addStripes(renderer, name, fill.color0, fill.color1, fill.width)
            is RenderHorizontalStripes -> addStripes(
                renderer,
                name,
                fill.color0,
                fill.color1,
                fill.width,
                " gradientTransform=\"rotate(90)\""
            )

            is RenderTiles -> addTiles(renderer, name, fill)
        }
    }

    private fun addStripes(
        renderer: SvgRenderer,
        name: String,
        color0: RenderColor,
        color1: RenderColor,
        width: Distance,
        options: String = "",
    ) {
        renderer.tag(
            "linearGradient",
            "id=\"%s\" spreadMethod=\"repeat\" x2=\"%.4f\" gradientUnits=\"userSpaceOnUse\"%s",
            name, width.toMeters(), options
        ) { tag ->
            addStop(tag, 0.0f, color0)
            addStop(tag, 0.5f, color0)
            addStop(tag, 0.5f, color1)
            addStop(tag, 1.0f, color1)
        }
    }

    private fun addStop(
        renderer: SvgRenderer,
        offset: Float,
        color: RenderColor,
    ) {
        renderer.selfClosingTag("stop", "offset=\"%.2f\" stop-color=\"%s\"", offset, toSvg(color))
    }

    private fun addCircles(
        renderer: SvgRenderer,
        name: String,
        circles: RenderCircles,
    ) {
        val width = circles.width.toMeters()

        renderer.tag(
            "pattern",
            "id=\"%s\" viewBox=\"0,0,100,100\" width=\"%s\" height=\"%s\" patternUnits=\"userSpaceOnUse\"",
            name, width, width
        ) { tag ->
            val full = AABB(Size2d.square(Distance.fromMeters(100)))
            val tile = full.shrink(FULL - circles.radiusPercentage)

            if (circles.background != null) {
                tag.renderRectangle(full, NoBorder(RenderSolid(circles.background)))
            }

            tag.renderCircle(tile, NoBorder(RenderSolid(circles.circle)))
        }
    }

    private fun addTiles(
        renderer: SvgRenderer,
        name: String,
        tiles: RenderTiles,
    ) {
        val width = tiles.width.toMeters()

        renderer.tag(
            "pattern",
            "id=\"%s\" viewBox=\"0,0,100,100\" width=\"%s\" height=\"%s\" patternUnits=\"userSpaceOnUse\"",
            name, width, width
        ) { tag ->
            val full = AABB(Size2d.square(Distance.fromMeters(100)))
            val tile = full.shrink(tiles.borderPercentage)

            if (tiles.background != null) {
                tag.renderRectangle(full, NoBorder(RenderSolid(tiles.background)))
            }

            tag.renderRectangle(tile, NoBorder(RenderSolid(tiles.fill)))
        }
    }

    private fun toSvg(color: RenderColor) = color.toCode()

}
