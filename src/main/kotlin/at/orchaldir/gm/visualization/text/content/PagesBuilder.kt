package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Orientation.Companion.zero
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.calculateLength
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.wrapString
import at.orchaldir.gm.utils.toInt

data class PageEntry(
    private var position: Point2d,
    private val line: String,
    private val options: RenderStringOptions,
    private val isLastLine: Boolean,
) {

    fun render(renderer: LayerRenderer, width: Distance) =
        if (options.horizontalAlignment == HorizontalAlignment.Justified && !isLastLine) {
            val lineLength = calculateLength(line, options.size)
            val diff = width.toMeters() - lineLength
            val words = line.split(' ')
            val step = Point2d(diff / (words.size - 1), 0.0f)
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

                    currentPosition += step.addWidth(Distance.fromMeters(calculateLength(text, options.size)))
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

    fun render(renderer: LayerRenderer, width: Distance) = entries
        .forEach { it.render(renderer, width) }

}

data class Pages(
    private val width: Distance,
    private val pages: List<Page>,
) {

    fun render(renderer: LayerRenderer, index: Int) = pages[index].render(renderer, width)

}

data class PagesBuilder(
    private val aabb: AABB,
    private var currentPosition: Point2d = aabb.start,
    private var currentPage: MutableList<PageEntry> = mutableListOf(),
    private val pages: MutableList<Page> = mutableListOf(),
) {

    fun addParagraph(string: String, options: RenderStringOptions): PagesBuilder {
        val step = Point2d(0.0f, options.size)
        val lines = wrapString(string, Distance.fromMeters(aabb.size.width), options.size)
        val lastIndex = lines.size - 1

        lines.withIndex().forEach {
            val isLastLine = it.index == lastIndex
            currentPage.add(PageEntry(currentPosition, it.value, options, isLastLine))

            currentPosition += step

            checkEndOfPage(options.size)
        }

        return this
    }

    fun addBreak(distance: Distance): PagesBuilder {
        currentPosition = currentPosition.addHeight(distance)

        checkEndOfPage()

        return this
    }

    fun addPageBreak(): PagesBuilder {
        if (currentPage.isNotEmpty()) {
            startNewPage()
        }

        return this
    }

    fun build() = Pages(
        Distance.fromMeters(aabb.size.width),
        pages + Page(currentPage),
    )

    fun count() = pages.size + currentPage.isNotEmpty().toInt()

    fun hasReached(factor: Factor) = ((currentPosition.y - aabb.start.y) / aabb.size.height) >= factor.toNumber()

    private fun checkEndOfPage(bonus: Float = 0.0f) {
        if (currentPosition.y + bonus >= aabb.getEnd().y) {
            startNewPage()
        }
    }

    private fun startNewPage() {
        currentPosition = aabb.start
        pages.add(Page(currentPage))
        currentPage = mutableListOf()
    }

}