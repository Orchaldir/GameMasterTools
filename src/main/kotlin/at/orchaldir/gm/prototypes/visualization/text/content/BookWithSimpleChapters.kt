package at.orchaldir.gm.prototypes.visualization.text.content

import at.orchaldir.gm.core.generator.TextGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.SolidFont
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.text.TEXT_CONFIG
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.text.content.visualizeAllPagesOfText
import java.io.File

private val ID = TextId(0)

fun main() {
    val font = SolidFont(Distance.fromMillimeters(10), Color.Blue)
    val book = Book(
        Hardcover(),
        page = ColorItemPart(Color.AntiqueWhite),
        size = Size2d.fromMillimeters(125, 190)
    )
    val generator = TextGenerator.create(TEXT_CONFIG.exampleStrings, 0)
    val style = ContentStyle()
    val chapter0 = SimpleChapter(0, generator.generateParagraphs(style, 10, 20))
    val chapter1 = SimpleChapter(1, generator.generateParagraphs(style, 10, 20))
    val chapters = SimpleChapters(
        listOf(chapter0, chapter1),
        pageNumbering = PageNumberingReusingFont(),
        tableOfContents = ComplexTableOfContents(titleOptions = font),
    )
    val text = Text(
        ID,
        format = book,
        content = chapters,
    )

    val svg = visualizeAllPagesOfText(State(), TEXT_CONFIG, text)!!

    File("book-simple-chapters.svg").writeText(svg.export())
}