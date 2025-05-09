package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.item.text.content.AbstractChapter
import at.orchaldir.gm.core.model.item.text.content.AbstractChapters
import at.orchaldir.gm.core.model.item.text.content.NoTableOfContents
import at.orchaldir.gm.core.model.item.text.content.SimpleTableOfContents
import at.orchaldir.gm.core.model.item.text.content.TocData
import at.orchaldir.gm.core.model.item.text.content.TocLine
import at.orchaldir.gm.core.model.name.NotEmptyString
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.HorizontalAlignment.Center
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions

fun visualizeTableOfContents(
    builder: PagesBuilder,
    chapters: AbstractChapters,
    titleOptions: RenderStringOptions,
    mainOptions: RenderStringOptions,
) {
    when (val toc = chapters.tableOfContents) {
        NoTableOfContents -> doNothing()
        is SimpleTableOfContents -> visualizeTableOfContents(
            builder,
            toc.title,
            chapters.chapters,
            titleOptions,
            mainOptions,
            toc.data,
            toc.line,
        )
    }
}

private fun visualizeTableOfContents(
    builder: PagesBuilder,
    title: NotEmptyString,
    chapters: List<AbstractChapter>,
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
        visualizeTocLine(
            builder,
            chapter,
            mainOptions,
            data,
            line,
            page,
        )

        page += chapter.content.pages
    }

    builder.addPageBreak()
}

private fun visualizeTocLine(
    builder: PagesBuilder,
    chapter: AbstractChapter,
    options: RenderStringOptions,
    data: TocData,
    line: TocLine,
    page: Int,
) = when (data) {
    TocData.NamePage -> builder.addTocEntry(chapter.title.text, page.toString(), line, options)
    TocData.IndexNamePage -> builder.addTocEntry(chapter.title.text, page.toString(), line, options)
    TocData.PageName -> builder.addTocEntry(page.toString(), chapter.title.text, line, options)
}.addBreak(options.size)