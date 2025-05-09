package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.utils.math.Orientation.Companion.zero
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.calculateLength
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions

data class PageEntry(
    private var position: Point2d,
    private val width: Distance,
    private val line: String,
    private val options: RenderStringOptions,
    private val isLastLine: Boolean = false,
) {

    fun render(renderer: LayerRenderer) =
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
                    val lastOptions = options.copy(horizontalAlignment = HorizontalAlignment.End)
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

    private fun simpleRender(renderer: LayerRenderer): LayerRenderer = renderer
        .renderString(line, position, zero(), options)

}

data class Page(
    private val entries: List<PageEntry>,
) {

    fun render(renderer: LayerRenderer) = entries
        .forEach { it.render(renderer) }

}

data class Pages(
    private val pages: List<Page>,
) {

    fun render(renderer: LayerRenderer, index: Int) = pages[index].render(renderer)

}
