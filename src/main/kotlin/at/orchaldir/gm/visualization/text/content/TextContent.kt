package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.content.TextContent
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.visualization.text.TextRenderConfig
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeAllPagesOfText(
    state: State,
    config: TextRenderConfig,
    text: Text,
) = when (text.format) {
    UndefinedTextFormat -> null
    is Book -> visualizeAllPagesOfBook(state, config, text, text.format)
    is Scroll -> visualizeAllPagesOfScroll(state, config, text, text.format)
}

fun visualizePageOfContent(
    state: State,
    config: TextRenderConfig,
    text: Text,
    page: Int,
) = when (text.format) {
    UndefinedTextFormat -> null
    is Book -> visualizePageOfBook(state, config, text, text.format, page)
    is Scroll -> visualizePageOfScroll(state, config, text, text.format, page)
}

fun visualizeTextContent(
    state: TextRenderState,
    format: TextFormat,
    content: TextContent,
    pageIndex: Int,
) {
    if (pageIndex >= content.pages()) {
        return
    }

    val inner = AABB.fromCenter(state.aabb.getCenter(), state.config.calculateOpenSize(format))
    val innerState = state.copy(aabb = inner)
    val pages = buildPages(innerState, content, pageIndex)!!

    when (format) {
        is Book -> visualizeBookPage(innerState, format, content, pages, pageIndex)
        is Scroll -> visualizeScrollContent(innerState, format, content, pages, listOf(pageIndex))
        UndefinedTextFormat -> doNothing()
    }
}
