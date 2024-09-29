package at.orchaldir.gm.utils.renderer.svg

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
            lines.add("  <defs>")

            patterns.forEach { (fill, name) -> addPatternLines(lines, fill, name) }

            lines.add("  </defs>")
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

    private fun toSvg(color: RenderColor) = color.toCode()

}
