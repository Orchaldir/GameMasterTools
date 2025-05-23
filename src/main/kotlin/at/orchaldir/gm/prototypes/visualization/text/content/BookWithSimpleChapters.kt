package at.orchaldir.gm.prototypes.visualization.text.content

import at.orchaldir.gm.core.generator.RarityGenerator
import at.orchaldir.gm.core.generator.TextGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.TextId
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.font.SolidFont
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.reducer.item.updatePageCount
import at.orchaldir.gm.prototypes.visualization.text.TEXT_CONFIG
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.text.content.visualizeAllPagesOfText
import java.io.File

private val ID = TextId(0)

fun main() {
    val state = State()
    val font = SolidFont(Distance.fromMillimeters(10), Color.Blue)
    val book = Book(
        Hardcover(),
        page = ColorItemPart(Color.AntiqueWhite),
        size = Size2d.fromMillimeters(125, 190)
    )
    val generator = TextGenerator.create(
        TEXT_CONFIG.exampleStrings,
        RarityGenerator.empty(5),
        0,
    )
    val style = ContentStyle(
        quote = SolidFont(DEFAULT_MAIN_SIZE, Color.Crimson),
    )
    val chapters = (0..<2).withIndex().map {
        SimpleChapter(it.index, generator.generateEntries(style, 6, 7))
    }
    val content = SimpleChapters(
        chapters,
        style,
        pageNumbering = PageNumberingReusingFont(),
        tableOfContents = ComplexTableOfContents(titleOptions = font),
    )
    val text = updatePageCount(
        state,
        TEXT_CONFIG,
        Text(
            ID,
            format = book,
            content = content,
        )
    )

    val svg = visualizeAllPagesOfText(State(), TEXT_CONFIG, text)!!

    File("book-simple-chapters.svg").writeText(svg.export())
}