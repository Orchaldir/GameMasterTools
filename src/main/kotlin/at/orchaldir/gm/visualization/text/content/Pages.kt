package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.item.text.content.TocLine
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.HorizontalAlignment.End
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.zero
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.calculateLength
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions

interface PageEntry {

    fun render(renderer: LayerRenderer)

}

data class StringPageEntry(
    private var position: Point2d,
    private val width: Distance,
    private val line: String,
    private val options: RenderStringOptions,
    private val isLastLine: Boolean = false,
) : PageEntry {

    override fun render(renderer: LayerRenderer) {
        if (options.horizontalAlignment == HorizontalAlignment.Justified && !isLastLine) {
            val lineLength = calculateLength(line, options.size)
            val diff = width - lineLength
            val words = line.split(' ')
            val step = Point2d.xAxis(diff / (words.size - 1))
            var currentPosition = position
            val lastIndex = words.size - 1

            words.withIndex().forEach { entry ->
                val word = entry.value

                if (lastIndex == entry.index) {
                    val lastOptions = options.copy(horizontalAlignment = End)
                    renderer.renderString(word, position.addWidth(width), zero(), lastOptions)
                } else {
                    val text = if (entry.index == 0) {
                        word
                    } else {
                        " $word"
                    }

                    renderer.renderString(text, currentPosition, zero(), options)

                    currentPosition += step.addWidth(calculateLength(text, options.size))
                }
            }
        } else {
            simpleRender(renderer)
        }
    }

    private fun simpleRender(renderer: LayerRenderer): LayerRenderer = renderer
        .renderString(line, position, zero(), options)

}

data class TocPageEntry(
    private var position: Point2d,
    private val width: Distance,
    private val left: String,
    private val right: String,
    private val line: TocLine,
    private val options: RenderStringOptions,
) : PageEntry {

    override fun render(renderer: LayerRenderer) {
        renderer
            .renderString(left, position, width, options)
            .renderString(right, position, width, options.copy(horizontalAlignment = End))

        when (line) {
            TocLine.Empty -> doNothing()
            TocLine.Line -> {
                val leftLength = calculateLength(left, options.size)
                val rightLength = calculateLength(right, options.size)
                val padding = leftLength.max(rightLength) * 0.5f
                val start = position.addHeight(options.size / 3.0f)
                val points = listOf(
                    start.addWidth(leftLength + padding),
                    start.addWidth(width - rightLength - padding),
                )
                val lineOptions = LineOptions(Color.Black.toRender(), options.size / 20.0f)

                renderer.renderLine(points, lineOptions)
            }

            TocLine.Dots -> renderSymbols(renderer, ".", 2)
            TocLine.SpacedDots -> renderSymbols(renderer, ". ", 3)
        }

    }

    private fun renderSymbols(renderer: LayerRenderer, symbol: String, divider: Int) {
        val leftLength = calculateLength(left, options.size)
        val rightLength = calculateLength(right, options.size)
        val dotLength = calculateLength('.', options.size)
        val lineLength = width - (leftLength + rightLength)
        val numberOfDots = (lineLength.toMeters() / dotLength.toMeters()).toInt() / divider
        val dots = symbol.repeat(numberOfDots)

        renderer.renderString(
            dots,
            position.addWidth(leftLength + lineLength / 2.0f),
            zero(),
            options.copy(horizontalAlignment = HorizontalAlignment.Center),
        )
    }
}

data class Page(
    private val entries: List<PageEntry>,
) {

    fun render(renderer: LayerRenderer) = entries
        .forEach { it.render(renderer) }

}

data class Pages(
    private val pages: List<Page>,
    val chapters: List<Int>,
) {

    fun render(renderer: LayerRenderer, index: Int) = pages[index].render(renderer)

}
