package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Orientation.Companion.zero
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.wrapString
import at.orchaldir.gm.utils.toInt

data class PageEntry(
    private var position: Point2d,
    private val line: String,
    private val options: RenderStringOptions,
) {

    fun render(renderer: LayerRenderer) = renderer
        .renderString(line, position, zero(), options)

}

data class Page(
    private val entries: List<PageEntry>,
) {

    fun render(renderer: LayerRenderer) = entries.forEach { it.render(renderer) }

}

data class Pages(
    private val pages: List<Page>,
) {

    fun render(renderer: LayerRenderer, index: Int) = pages[index].render(renderer)

}

data class PagesBuilder(
    private val aabb: AABB,
    private var currentPosition: Point2d = aabb.start,
    private var currentPage: MutableList<PageEntry> = mutableListOf(),
    private val pages: MutableList<Page> = mutableListOf(),
) {

    fun addString(string: String, options: RenderStringOptions): PagesBuilder {
        val step = Point2d(0.0f, options.size)
        val lines = wrapString(string, Distance.fromMeters(aabb.size.width), options.size)

        for (line in lines) {
            currentPage.add(PageEntry(currentPosition, line, options))

            currentPosition += step

            checkEndOfPage()
        }

        return this
    }

    fun addBreak(distance: Distance): PagesBuilder {
        currentPosition = currentPosition.addHeight(distance)

        checkEndOfPage()

        return this
    }

    fun build() = Pages(pages + Page(currentPage))

    fun count() = pages.size + currentPage.isNotEmpty().toInt()

    private fun checkEndOfPage() {
        if (currentPosition.y >= aabb.getEnd().y) {
            currentPosition = aabb.start
            pages.add(Page(currentPage))
            currentPage = mutableListOf()
        }
    }

}