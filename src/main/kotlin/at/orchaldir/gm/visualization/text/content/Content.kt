package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.generator.TextGenerator
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.CreatedByBusiness
import at.orchaldir.gm.core.model.util.CreatedByCharacter
import at.orchaldir.gm.core.model.util.CreatedByGod
import at.orchaldir.gm.core.model.util.CreatedByOrganization
import at.orchaldir.gm.core.model.util.CreatedByTown
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.HorizontalAlignment.Center
import at.orchaldir.gm.core.model.util.UndefinedCreator
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.model.convert
import at.orchaldir.gm.visualization.text.TextRenderState
import kotlin.math.min

fun buildPages(
    state: TextRenderState,
    content: TextContent,
    maxPageIndex: Int? = null,
): Pages? = when (content) {
    is AbstractChapters -> buildPagesForAbstractChapters(state, content, maxPageIndex)
    is AbstractText -> buildPagesForAbstractText(state, content, maxPageIndex)
    is SimpleChapters -> buildPagesForSimpleChapters(state, content, maxPageIndex)
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
    maxPageIndex: Int?,
): Pages {
    val margin = state.calculateMargin(content.style)
    val innerAABB = state.aabb.shrink(margin)
    val alignment = content.style.getHorizontalAlignment()
    val mainOptions = content.style.main.convert(state.state, VerticalAlignment.Top, alignment)
    val initialOptions = calculateInitialsOptions(state, mainOptions, content.style.initials)
    val builder = PagesBuilder(innerAABB)
    val generator = state.createTextGenerator()
    val maxPage = if (maxPageIndex != null) {
        min(content.content.pages, maxPageIndex + 2)
    } else {
        content.content.pages
    }

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
    maxPageIndex: Int,
) {
    val margin = state.calculateMargin(content.style)

    state.renderer.createGroup(state.aabb.start) { layer ->
        pages.render(layer, maxPageIndex)
    }

    visualizePageNumbering(state, margin, content.style, content.pageNumbering, maxPageIndex)
}

fun buildPagesForAbstractChapters(
    state: TextRenderState,
    content: AbstractChapters,
    maxPageIndex: Int?,
): Pages {
    val generator = state.createTextGenerator()

    return buildPagesForChapters(
        state,
        content.chapters,
        content.style,
        content.tableOfContents,
        maxPageIndex,
    ) { builder, mainOptions, _, initialOptions, _, maxPage ->
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
}

fun visualizeSimpleChapters(
    state: TextRenderState,
    content: SimpleChapters,
    pages: Pages,
    maxPageIndex: Int,
) {
    val margin = state.calculateMargin(content.style)

    state.renderer.createGroup(state.aabb.start) { layer ->
        pages.render(layer, maxPageIndex)
    }

    visualizePageNumbering(state, margin, content.style, content.pageNumbering, maxPageIndex)
}

fun buildPagesForSimpleChapters(
    state: TextRenderState,
    content: SimpleChapters,
    maxPageIndex: Int?,
) = buildPagesForChapters(
    state,
    content.chapters,
    content.style,
    content.tableOfContents,
    maxPageIndex,
) { builder, mainOptions, quoteOptions, initialOptions, chapter, _ ->
    chapter.entries.forEach { entry ->
        when (entry) {
            is Paragraph -> buildParagraphWithInitial(
                builder,
                mainOptions,
                initialOptions,
                entry.text.text,
                content.style.initials,
            )
            is Quote -> buildQuote(state, builder, entry, quoteOptions)
        }
    }
}

private fun buildQuote(
    state: TextRenderState,
    builder: PagesBuilder,
    quote: Quote,
    quoteOptions: RenderStringOptions,
) {
    builder.addParagraph("\"${quote.text.text}\"", quoteOptions)

    getQuoteSource(state, quote)?.let {
        builder.addParagraph(it, quoteOptions)
    }

    builder.addBreak(quoteOptions.size)
}

private fun getQuoteSource(
    state: TextRenderState,
    quote: Quote,
) = when (quote.source) {
    is CreatedByBusiness -> state.state.getElementName(quote.source.business)
    is CreatedByCharacter -> state.state.getElementName(quote.source.character)
    is CreatedByGod -> state.state.getElementName(quote.source.god)
    is CreatedByOrganization -> state.state.getElementName(quote.source.organization)
    is CreatedByTown -> state.state.getElementName(quote.source.town)
    UndefinedCreator -> null
}

private fun <C : Chapter> buildPagesForChapters(
    state: TextRenderState,
    chapters: List<C>,
    style: ContentStyle,
    tableOfContents: TableOfContents,
    maxPageIndex: Int?,
    buildChapter: (PagesBuilder, RenderStringOptions, RenderStringOptions, RenderStringOptions, C, Int) -> Unit,
): Pages {
    val margin = state.calculateMargin(style)
    val innerAABB = state.aabb.shrink(margin)
    val alignment = style.getHorizontalAlignment()
    val titleOptions = style.title.convert(state.state, VerticalAlignment.Top, HorizontalAlignment.Start)
    val mainOptions = style.main.convert(state.state, VerticalAlignment.Top, alignment)
    val quoteOptions = style.quote.convert(state.state, VerticalAlignment.Top, Center)
    val initialOptions = calculateInitialsOptions(state, mainOptions, style.initials)
    val builder = PagesBuilder(innerAABB)

    buildTableOfContents(
        state,
        builder,
        chapters,
        tableOfContents,
        titleOptions,
        mainOptions,
    )

    chapters.forEach { chapter ->
        val maxChapterPages = builder.count() + chapter.pages()
        val maxPage = if (maxPageIndex != null) {
            min(maxChapterPages, maxPageIndex + 2)
        } else {
            maxChapterPages
        }

        builder
            .addPageBreak()
            .startChapter()
            .addParagraph(chapter.title().text, titleOptions)
            .addBreak(style.main.getFontSize())

        buildChapter(
            builder,
            mainOptions,
            quoteOptions,
            initialOptions,
            chapter,
            maxPage,
        )

        builder.endChapter()
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
            generator.generateString(style.generation.main),
            style.initials,
        )
    }
}
