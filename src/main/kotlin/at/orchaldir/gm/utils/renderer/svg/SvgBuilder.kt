package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.AdvancedRenderer
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.*

class SvgBuilder(private val size: Size2d) : AdvancedRenderer {
    private val patterns: MutableMap<RenderFill, String> = mutableMapOf()
    private val layers: MutableMap<Int, MutableList<String>> = mutableMapOf()
    private val step: String = "  "

    fun finish(): Svg {
        val lines: MutableList<String> = mutableListOf()
        lines.add(getStartLine())

        if (patterns.isNotEmpty()) {
            val patternLines = mutableListOf<String>()
            val renderer = SvgRenderer(patterns, patternLines, step, step)

            renderer.tag("defs") { tag ->
                patterns.forEach { (fill, name) -> addPatternLines(tag, fill, name) }
            }

            lines.addAll(patternLines)
        }

        layers.toSortedMap()
            .values.forEach { layer -> lines.addAll(layer) }

        lines.add("</svg>")
        return Svg(lines)
    }

    // layers

    override fun getLayer(layer: Int): LayerRenderer = SvgRenderer(patterns, layers.computeIfAbsent(layer) {
        mutableListOf()
    }, step, step)

    // links

    override fun link(link: String, layerIndex: Int, content: (LayerRenderer) -> Unit) {
        val layer = SvgRenderer(patterns, layers.computeIfAbsent(layerIndex) { mutableListOf() }, step, step)

        layer.tag("a", "href=\"%s\" target=\"_parent\"", link) {
            content(it)
        }
    }

    override fun tooltip(text: String, layerIndex: Int, content: (LayerRenderer) -> Unit) {
        val layer = SvgRenderer(patterns, layers.computeIfAbsent(layerIndex) { mutableListOf() }, step, step, text)

        content(layer)
    }

    override fun linkAndTooltip(link: String, tooltip: String, layerIndex: Int, content: (LayerRenderer) -> Unit) {
        val layer = SvgRenderer(patterns, layers.computeIfAbsent(layerIndex) { mutableListOf() }, step, step, tooltip)

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
        size.width,
        size.height
    )

    private fun addPatternLines(renderer: SvgRenderer, fill: RenderFill, name: String) {
        when (fill) {
            is RenderSolid -> error("Solid is not a pattern!")
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
        width: UByte,
        options: String = "",
    ) {
        renderer.tag(
            "linearGradient",
            "id=\"%s\" spreadMethod=\"repeat\" x2=\"%s%%\" gradientUnits=\"userSpaceOnUse\"%s",
            name, width, options
        ) { tag ->
            addStop(tag, 0.0f, color0);
            addStop(tag, 0.5f, color0);
            addStop(tag, 0.5f, color1);
            addStop(tag, 1.0f, color1);
        }
    }

    private fun addStop(
        renderer: SvgRenderer,
        offset: Float,
        color: RenderColor,
    ) {
        renderer.selfClosingTag("stop", "offset=\"%.2f\" stop-color=\"%s\"", offset, toSvg(color))
    }

    private fun addTiles(
        renderer: SvgRenderer,
        name: String,
        tiles: RenderTiles,
    ) {
        renderer.tag(
            "pattern",
            "id=\"%s\" width=\"%s%%\" height=\"%s%%\" gradientUnits=\"userSpaceOnUse\"",
            name, tiles.width, tiles.width
        ) { tag ->
            val full = AABB(Size2d.square(tiles.width.toFloat()))
            val tile = full.shrink(Distance(tiles.border.toFloat()))

            if (tiles.background != null) {
                tag.renderRectangle(full, NoBorder(RenderSolid(tiles.background)))
            }

            tag.renderRectangle(tile, NoBorder(RenderSolid(tiles.fill)))
        }
    }

    private fun toSvg(color: RenderColor) = color.toCode()

}
