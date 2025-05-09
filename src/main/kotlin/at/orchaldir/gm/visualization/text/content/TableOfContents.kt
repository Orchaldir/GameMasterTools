package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.item.text.content.AbstractChapters
import at.orchaldir.gm.core.model.item.text.content.NoTableOfContents
import at.orchaldir.gm.core.model.item.text.content.SimpleTableOfContents
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions

fun visualizeTableOfContents(
    builder: PagesBuilder,
    chapters: AbstractChapters,
    titleOptions: RenderStringOptions,
    mainOptions: RenderStringOptions,
) {
    when (chapters.tableOfContents) {
        NoTableOfContents -> doNothing()
        is SimpleTableOfContents -> doNothing()
    }
}