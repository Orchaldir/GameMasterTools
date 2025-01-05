package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.book.BookCover
import at.orchaldir.gm.core.model.item.text.book.ComplexSewingPattern
import at.orchaldir.gm.core.model.item.text.book.ComplexStitch
import at.orchaldir.gm.core.model.item.text.book.CopticBinding
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.visualization.text.visualizeTextFormat
import java.io.File

private val ID = MaterialId(0)

fun main() {
    val cover = BookCover(Color.SaddleBrown, ID)
    val stitches = listOf(
        ComplexStitch(Color.Red, Size.Small, Size.Large),
        ComplexStitch(Color.Green, Size.Medium, Size.Small),
        ComplexStitch(Color.Blue, Size.Large, Size.Medium),
    )
    val book = Book(
        100,
        CopticBinding(cover, ComplexSewingPattern(stitches)),
        Size2i(125, 190)
    )

    val svg = visualizeTextFormat(TEXT_CONFIG, book)

    File("book-sewing-patterns-complex.svg").writeText(svg.export())
}