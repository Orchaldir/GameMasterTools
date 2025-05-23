package at.orchaldir.gm.prototypes.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.text.TEXT_CONFIG
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.visualization.text.content.visualizeAllPagesOfText
import java.io.File

private val ID = TextId(0)

fun main() {
    val book = Book(
        Hardcover(),
        page = ColorItemPart(Color.AntiqueWhite),
        size = Size2d.fromMillimeters(125, 190)
    )
    val content = AbstractText(
        AbstractContent(10),
        ContentStyle(initials = LargeInitials(MAX_INITIAL_SIZE))
    )
    val text = Text(
        ID,
        format = book,
        content = content,
    )

    val svg = visualizeAllPagesOfText(State(), TEXT_CONFIG, text)!!

    File("book-abstract-text.svg").writeText(svg.export())
}