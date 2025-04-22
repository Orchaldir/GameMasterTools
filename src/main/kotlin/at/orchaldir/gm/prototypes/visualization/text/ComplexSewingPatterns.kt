package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.ComplexSewingPattern
import at.orchaldir.gm.core.model.item.text.book.ComplexStitch
import at.orchaldir.gm.core.model.item.text.book.CopticBinding
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Color.Blue
import at.orchaldir.gm.core.model.util.Color.Green
import at.orchaldir.gm.core.model.util.Color.Red
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.visualization.text.visualizeTextFormat
import java.io.File

private val ID = MaterialId(0)

fun main() {
    val cover = FillItemPart(Color.SaddleBrown)
    val stitches = listOf(
        ComplexStitch(ColorItemPart(Red), Size.Small, Size.Large),
        ComplexStitch(ColorItemPart(Green), Size.Medium, Size.Small),
        ComplexStitch(ColorItemPart(Blue), Size.Large, Size.Medium),
    )
    val book = Book(
        100,
        ColorItemPart(),
        CopticBinding(cover, sewingPattern = ComplexSewingPattern(stitches)),
        Size2i.fromMillimeters(125, 190)
    )

    val svg = visualizeTextFormat(State(), TEXT_CONFIG, book)

    File("book-sewing-patterns-complex.svg").writeText(svg.export())
}