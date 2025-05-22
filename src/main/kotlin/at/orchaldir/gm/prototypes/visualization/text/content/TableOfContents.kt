package at.orchaldir.gm.prototypes.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.name.NotEmptyString.Companion.init
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
    val intro = AbstractChapter(init("Introduction"), AbstractContent(2))
    val conclusion = AbstractChapter(init("Conclusion"), AbstractContent(3))
    val chapters = mutableListOf(intro)
    repeat(5) {
        chapters.add(AbstractChapter(it, AbstractContent(3 * 2 * it)))
    }
    chapters.add(conclusion)

    renderTextContentTable(
        "book-toc.svg",
        State(),
        TEXT_CONFIG,
        book,
        addNames(TocLine.entries),
        addNames(TocData.entries),
    ) { line, data ->
        AbstractChapters(
            chapters,
            pageNumbering = PageNumberingReusingFont(),
            tableOfContents = SimpleTableOfContents(data, line),
        )
    }
}