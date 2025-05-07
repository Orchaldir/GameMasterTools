package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.content.AbstractText
import at.orchaldir.gm.utils.math.Size2i.Companion.fromMillimeters
import at.orchaldir.gm.visualization.text.visualizeTextContent
import java.io.File

private val ID = TextId(0)

fun main() {
    val book = Book(
        Hardcover(),
        size = fromMillimeters(125, 190)
    )
    val content = AbstractText()
    val text = Text(
        ID,
        format = book,
        content = content,
    )

    val svg = visualizeTextContent(State(), TEXT_CONFIG, text, 0)

    File("book-page.svg").writeText(svg.export())
}