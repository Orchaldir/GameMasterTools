package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.HorizontalAlignment.Center
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.model.convert
import at.orchaldir.gm.visualization.text.TextRenderState

fun buildTableOfContents(
    state: TextRenderState,
    builder: PagesBuilder,
    chapters: List<Chapter>,
    toc: TableOfContents,
    titleOptions: RenderStringOptions,
    mainOptions: RenderStringOptions,
) {
    when (toc) {
        NoTableOfContents -> doNothing()
        is SimpleTableOfContents -> buildTableOfContents(
            builder,
            toc.title,
            chapters,
            titleOptions,
            mainOptions,
            toc.data,
            toc.line,
        )

        is ComplexTableOfContents -> buildTableOfContents(
            builder,
            toc.title,
            chapters,
            toc.titleOptions.convert(state.state),
            toc.mainOptions.convert(state.state, horizontalAlignment = HorizontalAlignment.Start),
            toc.data,
            toc.line,
        )
    }
}

private fun buildTableOfContents(
    builder: PagesBuilder,
    title: NotEmptyString,
    chapters: List<Chapter>,
    titleOptions: RenderStringOptions,
    mainOptions: RenderStringOptions,
    data: TocData,
    line: TocLine,
) {
    builder
        .addParagraph(title.text, titleOptions.copy(horizontalAlignment = Center))
        .addBreak(mainOptions.size)
    var page = 2

    chapters.forEach { chapter ->
        buildTocLine(
            builder,
            chapter,
            mainOptions,
            data,
            line,
            page,
        )

        page += chapter.pages()
    }

    builder.addPageBreak()
}

private fun buildTocLine(
    builder: PagesBuilder,
    chapter: Chapter,
    options: RenderStringOptions,
    data: TocData,
    line: TocLine,
    page: Int,
) = when (data) {
    TocData.NamePage -> builder.addTocEntry(chapter.title().text, page.toString(), line, options)
    TocData.PageName -> builder.addTocEntry(page.toString(), chapter.title().text, line, options)
}.addBreak(options.size)