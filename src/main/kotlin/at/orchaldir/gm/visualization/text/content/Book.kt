package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.text.TextRenderConfig
import at.orchaldir.gm.visualization.text.TextRenderState
import at.orchaldir.gm.visualization.text.resolveTextData

fun visualizeAllPagesOfBook(
    state: State,
    config: TextRenderConfig,
    text: Text,
    book: Book,
) = visualizeBookContent(
    state,
    config,
    text,
    book,
    (0..<text.content.pages()).toList(),
)

fun visualizePageOfBook(
    state: State,
    config: TextRenderConfig,
    text: Text,
    book: Book,
    pageIndex: Int,
) = visualizeBookContent(
    state,
    config,
    text,
    book,
    listOf(pageIndex),
)

fun visualizeBookContent(
    state: State,
    config: TextRenderConfig,
    text: Text,
    book: Book,
    pagesIndices: List<Int>,
): Svg {
    val numberOfPages = pagesIndices.size
    val pageSize = config.calculateClosedSize(text.format)
    val contentSize = Size2d(pageSize.width * numberOfPages, pageSize.height)
    val paddedPageSize = config.addPadding(pageSize)
    val paddedContentSize = config.addPadding(contentSize)
    val builder = SvgBuilder(paddedContentSize)
    val data = resolveTextData(state, text)
    var start = Point2d()
    val step = Point2d.xAxis(pageSize.width)
    val builderState = TextRenderState(state, AABB(pageSize), config, builder, data)
    val pages = buildPages(builderState, text.content, pagesIndices.last())!!

    builder.getLayer().renderRectangle(AABB(paddedContentSize), BorderOnly(config.line))

    pagesIndices.forEach { pageIndex ->
        val paddedAabb = AABB(start, paddedPageSize)
        val inner = AABB.fromCenter(paddedAabb.getCenter(), pageSize)
        val renderState = TextRenderState(state, inner, config, builder, data)

        visualizeBookPage(renderState, book, text.content, pages, pageIndex)

        start += step
    }

    return builder.finish()
}

fun visualizeBookPage(
    state: TextRenderState,
    book: Book,
    content: TextContent,
    pages: Pages,
    page: Int,
) {
    visualizeBlankBookPage(state, book)

    when (content) {
        is AbstractText -> visualizeAbstractText(state, content, pages, page)
        is AbstractChapters -> visualizeAbstractChapters(state, content, pages, page)
        is SimpleChapters -> visualizeSimpleChapters(state, content, pages, page)
        UndefinedTextContent -> doNothing()
    }
}

private fun visualizeBlankBookPage(
    state: TextRenderState,
    book: Book,
) {
    val color = book.page.getColor(state.state)
    val options = state.config.getLineOptions(color)

    state.renderer.getLayer().renderRectangle(state.aabb, options)
}
