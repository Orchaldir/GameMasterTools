package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.text.TextRenderConfig
import at.orchaldir.gm.visualization.text.TextRenderState
import at.orchaldir.gm.visualization.text.resolveTextData
import at.orchaldir.gm.visualization.text.scroll.visualizeOpenScroll

fun visualizeAllPagesOfScroll(
    state: State,
    config: TextRenderConfig,
    text: Text,
    scroll: Scroll,
) = visualizeScrollContent(
    state,
    config,
    text,
    scroll,
    (0..<text.content.pages()).toList(),
)

fun visualizePageOfScroll(
    state: State,
    config: TextRenderConfig,
    text: Text,
    scroll: Scroll,
    pageIndex: Int,
) = visualizeScrollContent(
    state,
    config,
    text,
    scroll,
    listOf(pageIndex),
)

private fun visualizeScrollContent(
    state: State,
    config: TextRenderConfig,
    text: Text,
    scroll: Scroll,
    pagesIndices: List<Int>,
): Svg {
    val numberOfPages = pagesIndices.size
    val contentSize = scroll.calculateOpenSize(numberOfPages)
    val pageSize = scroll.calculatePageSize()
    val paddedContentSize = config.addPadding(contentSize)
    val builder = SvgBuilder(paddedContentSize)
    val data = resolveTextData(state, text)
    val paddedAabb = AABB(paddedContentSize)
    val scrollAabb = AABB.fromCenter(paddedAabb.getCenter(), contentSize)
    val renderState = TextRenderState(state, scrollAabb, config, builder, data)
    val builderState = TextRenderState(state, AABB(pageSize), config, builder, data)
    val pages = buildPages(builderState, text.content, pagesIndices.last())!!

    builder.getLayer().renderRectangle(AABB(paddedContentSize), BorderOnly(config.line))

    visualizeScrollContent(
        renderState,
        scroll,
        text.content,
        pages,
        pagesIndices,
    )

    return builder.finish()
}

fun visualizeScrollContent(
    state: TextRenderState,
    scroll: Scroll,
    content: TextContent,
    pages: Pages,
    pagesIndices: List<Int>,
) {
    val numberOfPages = pagesIndices.size
    val pageSize = scroll.calculatePageSize()
    val scrollAabb = state.aabb

    visualizeOpenScroll(state, scroll)

    val pageColor = scroll.main.getColor(state.state)
    val pageOptions = FillAndBorder(pageColor.toRender(), state.config.line)
    val pagesStart = scrollAabb.start + Point2d(scroll.calculateWidthOfOneRod(), scroll.calculateHandleLength())
    val pagesSize = pageSize.replaceWidth(pageSize.width * numberOfPages)
    val pagesAabb = AABB(pagesStart, pagesSize)

    state.renderer.getLayer().renderRectangle(pagesAabb, pageOptions)

    var start = pagesStart
    val step = Point2d.xAxis(pageSize.width)

    pagesIndices.forEach { pageIndex ->
        val pageAabb = AABB(start, pageSize)
        val renderState = state.copy(aabb = pageAabb)

        when (content) {
            is AbstractText -> visualizeAbstractText(renderState, content, pages, pageIndex)
            is AbstractChapters -> visualizeAbstractChapters(renderState, content, pages, pageIndex)
            is SimpleChapters -> visualizeSimpleChapters(renderState, content, pages, pageIndex)
            UndefinedTextContent -> doNothing()
        }

        start += step
    }
}
