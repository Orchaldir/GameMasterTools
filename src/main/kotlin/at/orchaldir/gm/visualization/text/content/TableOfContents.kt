package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.item.text.content.AbstractChapter
import at.orchaldir.gm.core.model.item.text.content.AbstractChapters
import at.orchaldir.gm.core.model.item.text.content.NoTableOfContents
import at.orchaldir.gm.core.model.item.text.content.SimpleTableOfContents
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
        )
    }
}

private fun visualizeTableOfContents(
    builder: PagesBuilder,
    title: NotEmptyString,
    chapters: List<AbstractChapter>,
    titleOptions: RenderStringOptions,
    mainOptions: RenderStringOptions,
) {
    builder
        .addParagraph(title.text, titleOptions)
        .addBreak(mainOptions.size)
        .addPageBreak()
}