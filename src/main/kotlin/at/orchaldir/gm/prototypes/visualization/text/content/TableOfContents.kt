package at.orchaldir.gm.prototypes.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.text.TEXT_CONFIG
import at.orchaldir.gm.prototypes.visualization.text.renderTextContentTable
import at.orchaldir.gm.utils.math.Size2d

fun main() {
    val book = Book(
        Hardcover(),
        page = ColorItemPart(Color.AntiqueWhite),
        size = Size2d.fromMillimeters(125, 190)
    )
    val chapter0 = AbstractChapter(0, AbstractContent(2))
    val chapter1 = AbstractChapter(1, AbstractContent(3))
    val chapters = listOf(chapter0, chapter1)

    renderTextContentTable(
        "book-toc.svg",
        State(),
        TEXT_CONFIG,
        book,
        addNames(TocData.entries),
        addNames(TocLine.entries),
    ) { data, line ->
        AbstractChapters(
            chapters,
            pageNumbering = PageNumberingReusingFont(),
            tableOfContents = SimpleTableOfContents(data, line),
        )
    }
}