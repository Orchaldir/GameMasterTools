package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.content.AbstractChapter
import at.orchaldir.gm.core.model.item.text.content.AbstractChapters
import at.orchaldir.gm.core.model.item.text.content.AbstractContent
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Size2i.Companion.fromMillimeters
import at.orchaldir.gm.visualization.text.content.visualizeTextContent
import java.io.File

private val ID = TextId(0)

fun main() {
    val book = Book(
        Hardcover(),
        page = ColorItemPart(Color.AntiqueWhite),
        size = fromMillimeters(125, 190)
    )
    val chapter = AbstractChapter(0, AbstractContent(10))
    val chapters = AbstractChapters(listOf(chapter))
    val text = Text(
        ID,
        format = book,
        content = chapters,
    )

    val svg = visualizeTextContent(State(), TEXT_CONFIG, text, 0)

    File("book-page.svg").writeText(svg.export())
}