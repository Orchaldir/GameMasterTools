package at.orchaldir.gm.prototypes.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.content.AbstractChapter
import at.orchaldir.gm.core.model.item.text.content.AbstractChapters
import at.orchaldir.gm.core.model.item.text.content.AbstractContent
import at.orchaldir.gm.core.model.item.text.content.PageNumberingReusingFont
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.text.TEXT_CONFIG
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.visualization.text.content.visualizeTextContent
import java.io.File

private val ID = TextId(0)

fun main() {
    val book = Book(
        Hardcover(),
        page = ColorItemPart(Color.AntiqueWhite),
        size = Size2d.fromMillimeters(125, 190)
    )
    val chapter0 = AbstractChapter(0, AbstractContent(2))
    val chapter1 = AbstractChapter(1, AbstractContent(3))
    val chapters = AbstractChapters(
        listOf(chapter0, chapter1),
        pageNumbering = PageNumberingReusingFont()
    )
    val text = Text(
        ID,
        format = book,
        content = chapters,
    )

    val svg = visualizeTextContent(State(), TEXT_CONFIG, text)

    File("book-abstract-chapters-pages.svg").writeText(svg.export())
}