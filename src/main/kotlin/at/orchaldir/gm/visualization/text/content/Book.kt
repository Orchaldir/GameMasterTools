package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
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
    val alignment = content.style.getHorizontalAlignment()
    val mainOptions = content.style.main.convert(state.state, VerticalAlignment.Top, alignment)
    val initialOptions = calculateInitialsOptions(state, mainOptions, content.style.initials)
    val builder = PagesBuilder(innerAABB)
    val maxPage = min(content.content.pages, page + 2)

    visualizeAbstractContent(state, builder, content.style, mainOptions, initialOptions, maxPage)

    builder
        .build()
        .render(state.renderer.getLayer(), page)

    visualizePageNumbering(state, margin, content.style, content.pageNumbering, page)
}

private fun visualizeAbstractChapters(
    state: TextRenderState,
    content: AbstractChapters,
    page: Int,
) {
    val margin = state.aabb.convertMinSide(content.style.margin)
    val innerAABB = state.aabb.shrink(margin)
    val alignment = content.style.getHorizontalAlignment()
    val titleOptions = content.style.title.convert(state.state, VerticalAlignment.Top, HorizontalAlignment.Start)
    val mainOptions = content.style.main.convert(state.state, VerticalAlignment.Top, alignment)
    val initialOptions = calculateInitialsOptions(state, mainOptions, content.style.initials)
    val builder = PagesBuilder(innerAABB)

    visualizeTableOfContents(state, builder, content, titleOptions, mainOptions)

    content.chapters.forEach { chapter ->
        val maxPage = min(builder.count() + chapter.content.pages, page + 2)

        builder
            .addPageBreak()
            .addParagraph(chapter.title.text, titleOptions)
            .addBreak(content.style.main.getFontSize())

        visualizeAbstractContent(state, builder, content.style, mainOptions, initialOptions, maxPage)
    }

    builder
        .build()
        .render(state.renderer.getLayer(), page)

    visualizePageNumbering(state, margin, content.style, content.pageNumbering, page)
}

private fun visualizeAbstractContent(
    state: TextRenderState,
    builder: PagesBuilder,
    style: ContentStyle,
    mainOptions: RenderStringOptions,
    initialOptions: RenderStringOptions,
    maxPage: Int,
) {
    while (builder.count() < maxPage) {
        visualizeParagraphWithInitial(
            builder,
            mainOptions,
            initialOptions,
            state.config.exampleString,
            style.initials,
        )
    }

    while (!builder.hasReached(state.config.lastPageFillFactor)) {
        visualizeParagraphWithInitial(
            builder,
            mainOptions,
            initialOptions,
            state.config.exampleString,
            style.initials,
        )
    }
}

