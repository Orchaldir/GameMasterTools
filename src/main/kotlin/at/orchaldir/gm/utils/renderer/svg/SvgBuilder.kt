package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.utils.renderer.LinkRenderer
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.TooltipRenderer
import at.orchaldir.gm.utils.renderer.model.*


class SvgBuilder(private val size: Size2d) : LinkRenderer, TooltipRenderer {
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

    // layers

    override fun getLayer(layer: Int): LayerRenderer = SvgRenderer(patterns, layers.computeIfAbsent(layer) {
        mutableListOf()
    })

    // links

    override fun link(link: String, layerIndex: Int, content: (LayerRenderer) -> Unit) {
        val layer = layers.computeIfAbsent(layerIndex) { mutableListOf() }

        layer.add(String.format(LOCALE, "  <a href=\"%s\" target=\"_parent\">", link))

        content(SvgRenderer(patterns, layer))

        layer.add("  </a>")
    }

    // tooltips

    override fun tooltip(text: String, layerIndex: Int, content: (LayerRenderer) -> Unit) {
        TODO("Not yet implemented")
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
