package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.content.AbstractChapters
import at.orchaldir.gm.core.model.item.text.content.AbstractText
import at.orchaldir.gm.core.model.item.text.content.TextContent
import at.orchaldir.gm.core.model.item.text.content.UndefinedTextContent
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeBookPage(
    state: TextRenderState,
    book: Book,
    content: TextContent,
    page: Int,
) {
    visualizeBookPage(state, book)

    when (content) {
        is AbstractText -> visualizeAbstractText(state, content, page)
        is AbstractChapters -> visualizeAbstractChapters(state, content, page)
        UndefinedTextContent -> doNothing()
    }
}

private fun visualizeBookPage(
    state: TextRenderState,
    book: Book,
) {
    val color = book.page.getColor(state.state)
    val options = FillAndBorder(color.toRender(), state.config.line)

    state.renderer.getLayer().renderRectangle(state.aabb, options)
}
