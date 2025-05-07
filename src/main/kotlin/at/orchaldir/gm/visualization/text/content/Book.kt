package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.content.AbstractChapters
import at.orchaldir.gm.core.model.item.text.content.AbstractText
import at.orchaldir.gm.core.model.item.text.content.TextContent
import at.orchaldir.gm.core.model.item.text.content.UndefinedTextContent
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.convert
import at.orchaldir.gm.utils.renderer.renderWrappedString
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeBookPage(
    state: TextRenderState,
    book: Book,
    content: TextContent,
    page: Int,
) {
    visualizePage(state, book)

    when (content) {
        is AbstractText -> visualizeAbstractText(state, book, content, page)
        is AbstractChapters -> visualizeAbstractChapters(state, book, content, page)
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
    book: Book,
    content: AbstractText,
    page: Int,
) {
    val margin = state.aabb.convertMinSide(content.style.margin)
    val innerAABB = state.aabb.shrink(margin)
    val options = content.style.main.convert(state.state, VerticalAlignment.Top, HorizontalAlignment.Start)

    PagesBuilder(innerAABB)
        .addString(state.config.exampleString, options)
        .addBreak(content.style.main.getFontSize())
        .addString(state.config.exampleString, options)
        .build()
        .render(state.renderer.getLayer(), page)
}

private fun visualizeAbstractChapters(
    state: TextRenderState,
    book: Book,
    content: AbstractChapters,
    page: Int,
) {
    val margin = state.aabb.convertMinSide(content.style.margin)
    val innerAABB = state.aabb.shrink(margin)
    val titleOptions = content.style.title.convert(state.state, VerticalAlignment.Top, HorizontalAlignment.Start)
    val mainOptions = content.style.main.convert(state.state, VerticalAlignment.Top, HorizontalAlignment.Start)

    PagesBuilder(innerAABB)
        .addString(content.chapters[0].title.text, titleOptions)
        .addBreak(content.style.main.getFontSize())
        .addString(state.config.exampleString, mainOptions)
        .addBreak(content.style.main.getFontSize())
        .addString(state.config.exampleString, mainOptions)
        .build()
        .render(state.renderer.getLayer(), page)
}

