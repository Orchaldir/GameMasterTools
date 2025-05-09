package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Orientation.Companion.zero
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.utils.math.unit.ZERO
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.calculateLength
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.model.convert
import at.orchaldir.gm.utils.renderer.wrapString
import at.orchaldir.gm.utils.toInt
import kotlin.math.ceil

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
            val step = Point2d(diff.toMeters() / (words.size - 1), 0.0f)
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

data class PagesBuilder(
    private val state: State,
    private val aabb: AABB,
    private val width: Distance = fromMeters(aabb.size.width),
    private var currentPosition: Point2d = aabb.start,
    private var currentPage: MutableList<PageEntry> = mutableListOf(),
    private val pages: MutableList<Page> = mutableListOf(),
) {
    fun addParagraphWithInitial(
        string: String,
        options: RenderStringOptions,
        initials: Initials,
    ) = when (initials) {
        NormalInitials -> addParagraph(string, options)
        is LargeInitials -> {
            val initialSize = options.size * initials.size.toNumber()
            addParagraphWithInitial(
                string,
                options,
                options.copy(size = initialSize),
                initials.position,
            )
        }

        is FontInitials -> addParagraphWithInitial(
            string,
            options,
            initials.fontOption.convert(state, VerticalAlignment.Top),
            initials.position,
        )
    }

    private fun addParagraphWithInitial(
        string: String,
        mainOptions: RenderStringOptions,
        initialOptions: RenderStringOptions,
        position: InitialPosition,
    ): PagesBuilder {
        val initialChar = string.take(1)
        val rest = string.drop(1)
        val initialLength = calculateLength(initialChar, initialOptions.size) * 1.5f

        val updatedInitialOptions = when (position) {
            InitialPosition.Baseline -> initialOptions.copy(horizontalAlignment = HorizontalAlignment.Start)
            InitialPosition.Margin -> initialOptions.copy(horizontalAlignment = HorizontalAlignment.End)
            InitialPosition.DropCap -> initialOptions.copy(horizontalAlignment = HorizontalAlignment.Start)
        }

        currentPage.add(PageEntry(currentPosition, width, initialChar, updatedInitialOptions))

        when (position) {
            InitialPosition.Baseline -> {
                val updatedInitialSize = initialOptions.size * 0.8f
                if (updatedInitialSize > mainOptions.size) {
                    addBreak(updatedInitialSize - mainOptions.size)
                }
                addParagraph(
                    rest,
                    mainOptions,
                    1,
                    initialLength,
                )
            }

            InitialPosition.Margin -> addParagraph(rest, mainOptions)
            InitialPosition.DropCap -> addParagraph(
                rest,
                mainOptions,
                ceil(initialOptions.size.toMeters() / mainOptions.size.toMeters()).toInt(),
                initialLength,
            )
        }

        return this
    }

    fun addParagraph(
        string: String,
        options: RenderStringOptions,
        indentedLines: Int = 0,
        indentedDistance: Distance = ZERO,
    ): PagesBuilder {
        val step = Point2d(0.0f, options.size.toMeters())
        val lines = wrapString(
            string,
            fromMeters(aabb.size.width),
            options.size,
            indentedLines,
            indentedDistance,
        )
        val lastIndex = lines.size - 1

        lines.withIndex().forEach {
            val isLastLine = it.index == lastIndex

            val entry = if (it.index < indentedLines) {
                PageEntry(
                    currentPosition.addWidth(indentedDistance),
                    width - indentedDistance,
                    it.value,
                    options,
                    isLastLine
                )
            } else {
                PageEntry(currentPosition, width, it.value, options, isLastLine)
            }

            currentPage.add(entry)


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
        pages + Page(currentPage),
    )

    fun count() = pages.size + currentPage.isNotEmpty().toInt()

    fun hasReached(factor: Factor) = ((currentPosition.y - aabb.start.y) / aabb.size.height) >= factor.toNumber()

    private fun checkEndOfPage(bonus: Distance = ZERO) {
        if (currentPosition.y + bonus.toMeters() >= aabb.getEnd().y) {
            startNewPage()
        }
    }

    private fun startNewPage() {
        currentPosition = aabb.start
        pages.add(Page(currentPage))
        currentPage = mutableListOf()
    }

}