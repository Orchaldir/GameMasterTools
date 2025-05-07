package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.content.AbstractChapters
import at.orchaldir.gm.core.model.item.text.content.AbstractText
import at.orchaldir.gm.core.model.item.text.content.TextContent
import at.orchaldir.gm.core.model.item.text.content.UndefinedTextContent
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.convert
import at.orchaldir.gm.visualization.text.TextRenderState
import kotlin.math.min

fun visualizeBookPage(
    state: TextRenderState,
    book: Book,
    content: TextContent,
    page: Int,
) {
    visualizePage(state, book)

    when (content) {
        is AbstractText -> visualizeAbstractText(state, content, page)
        is AbstractChapters -> visualizeAbstractChapters(state, content, page)
        UndefinedTextContent -> doNothing()
    }
}

private fun visualizePage(
    state: TextRenderState,
    book: Book,
) {
    val color = book.page.getColor(state.state)
    val options = FillAndBorder(color.toRender(), state.config.line)

    state.renderer.getLayer().renderRectangle(state.aabb, options)
}

private fun visualizeAbstractText(
    state: TextRenderState,
    content: AbstractText,
    page: Int,
) {
    val margin = state.aabb.convertMinSide(content.style.margin)
    val innerAABB = state.aabb.shrink(margin)
    val options = content.style.main.convert(state.state, VerticalAlignment.Top, HorizontalAlignment.Start)
    val builder = PagesBuilder(innerAABB)
    val maxPage = min(content.content.pages, page + 1)

    while (builder.count() <= maxPage) {
        builder
            .addString(state.config.exampleString, options)
            .addBreak(content.style.main.getFontSize())
    }

    builder
        .build()
        .render(state.renderer.getLayer(), page)
}

private fun visualizeAbstractChapters(
    state: TextRenderState,
    content: AbstractChapters,
    page: Int,
) {
    val margin = state.aabb.convertMinSide(content.style.margin)
    val innerAABB = state.aabb.shrink(margin)
    val titleOptions = content.style.title.convert(state.state, VerticalAlignment.Top, HorizontalAlignment.Start)
    val mainOptions = content.style.main.convert(state.state, VerticalAlignment.Top, HorizontalAlignment.Start)
    val builder = PagesBuilder(innerAABB)

    content.chapters.forEach { chapter ->
        val maxPage = builder.count() + chapter.content.pages

        builder
            .addLineBreak()
            .addString(chapter.title.text, titleOptions)
            .addBreak(content.style.main.getFontSize())

        while (builder.count() < maxPage) {
            builder
                .addString(state.config.exampleString, mainOptions)
                .addBreak(content.style.main.getFontSize())
        }

        while (!builder.hasReached(state.config.lastPageFillFactor)) {
            builder
                .addString(state.config.exampleString, mainOptions)
                .addBreak(content.style.main.getFontSize())
        }
    }

    builder
        .build()
        .render(state.renderer.getLayer(), page)
}

