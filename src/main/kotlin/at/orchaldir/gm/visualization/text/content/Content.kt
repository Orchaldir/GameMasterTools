package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.generator.TextGenerator
import at.orchaldir.gm.core.model.item.text.content.AbstractChapters
import at.orchaldir.gm.core.model.item.text.content.AbstractText
import at.orchaldir.gm.core.model.item.text.content.SimpleChapters
import at.orchaldir.gm.core.model.item.text.content.ContentStyle
import at.orchaldir.gm.core.model.item.text.content.TextContent
import at.orchaldir.gm.core.model.item.text.content.UndefinedTextContent
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.model.convert
import at.orchaldir.gm.visualization.text.TextRenderState
import kotlin.math.min

fun buildPages(
    state: TextRenderState,
    content: TextContent,
    maxPageIndex: Int,
): Pages? = when (content) {
    is AbstractChapters -> buildPagesForAbstractChapters(state, content, maxPageIndex)
    is AbstractText -> buildPagesForAbstractText(state, content, maxPageIndex)
    is SimpleChapters -> TODO()
    UndefinedTextContent -> null
}

fun visualizeAbstractText(
    state: TextRenderState,
    content: AbstractText,
    pages: Pages,
    pageIndex: Int,
) {
    val margin = state.calculateMargin(content.style)

    state.renderer.createGroup(state.aabb.start) { layer ->
        pages.render(layer, pageIndex)
    }

    visualizePageNumbering(state, margin, content.style, content.pageNumbering, pageIndex)
}

fun buildPagesForAbstractText(
    state: TextRenderState,
    content: AbstractText,
    maxPageIndex: Int,
): Pages {
    val margin = state.calculateMargin(content.style)
    val innerAABB = state.aabb.shrink(margin)
    val alignment = content.style.getHorizontalAlignment()
    val mainOptions = content.style.main.convert(state.state, VerticalAlignment.Top, alignment)
    val initialOptions = calculateInitialsOptions(state, mainOptions, content.style.initials)
    val builder = PagesBuilder(innerAABB)
    val generator = state.createTextGenerator()
    val maxPage = min(content.content.pages, maxPageIndex + 2)

    buildAbstractContent(
        state,
        generator,
        builder,
        content.style,
        mainOptions,
        initialOptions,
        maxPage,
    )

    return builder
        .build()
}

fun visualizeAbstractChapters(
    state: TextRenderState,
    content: AbstractChapters,
    pages: Pages,
    pageIndex: Int,
) {
    val margin = state.calculateMargin(content.style)

    state.renderer.createGroup(state.aabb.start) { layer ->
        pages.render(layer, pageIndex)
    }

    visualizePageNumbering(state, margin, content.style, content.pageNumbering, pageIndex)
}

fun buildPagesForAbstractChapters(
    state: TextRenderState,
    content: AbstractChapters,
    pageIndex: Int,
): Pages {
    val margin = state.calculateMargin(content.style)
    val innerAABB = state.aabb.shrink(margin)
    val alignment = content.style.getHorizontalAlignment()
    val titleOptions = content.style.title.convert(state.state, VerticalAlignment.Top, HorizontalAlignment.Start)
    val mainOptions = content.style.main.convert(state.state, VerticalAlignment.Top, alignment)
    val initialOptions = calculateInitialsOptions(state, mainOptions, content.style.initials)
    val builder = PagesBuilder(innerAABB)
    val generator = state.createTextGenerator()

    buildTableOfContents(state, builder, content, titleOptions, mainOptions)

    content.chapters.forEach { chapter ->
        val maxPage = min(builder.count() + chapter.content.pages, pageIndex + 2)

        builder
            .addPageBreak()
            .addParagraph(chapter.title.text, titleOptions)
            .addBreak(content.style.main.getFontSize())

        buildAbstractContent(
            state,
            generator,
            builder,
            content.style,
            mainOptions,
            initialOptions,
            maxPage,
        )
    }

    return builder
        .build()
}

private fun buildAbstractContent(
    state: TextRenderState,
    generator: TextGenerator,
    builder: PagesBuilder,
    style: ContentStyle,
    mainOptions: RenderStringOptions,
    initialOptions: RenderStringOptions,
    maxPage: Int,
) {
    while (builder.count() < maxPage || !builder.hasReached(state.config.lastPageFillFactor)) {
        buildParagraphWithInitial(
            builder,
            mainOptions,
            initialOptions,
            generator.generateParagraphAsString(style),
            style.initials,
        )
    }
}
