package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.item.text.content.AbstractChapter
import at.orchaldir.gm.core.model.item.text.content.AbstractChapters
import at.orchaldir.gm.core.model.item.text.content.NoTableOfContents
import at.orchaldir.gm.core.model.item.text.content.SimpleTableOfContents
import at.orchaldir.gm.core.model.item.text.content.TocData
import at.orchaldir.gm.core.model.item.text.content.TocLine
import at.orchaldir.gm.core.model.name.NotEmptyString
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
        .addParagraph(title.text, titleOptions)
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
) = builder
    .addParagraph("${chapter.title.text} $page", options)
    .addBreak(options.size)