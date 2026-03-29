package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.item.common.ComplexSewingPattern
import at.orchaldir.gm.core.model.item.common.ComplexStitch
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.CopticBinding
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.MadeFromCord
import at.orchaldir.gm.core.model.util.part.MadeFromLeather
import at.orchaldir.gm.core.model.util.render.Color.*
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.visualization.text.visualizeTextFormat
import java.io.File

private val ID = MaterialId(0)

fun main() {
    val cover = MadeFromLeather(SaddleBrown)
    val stitches = listOf(
        ComplexStitch(MadeFromCord(Red), Size.Small, Size.Large),
        ComplexStitch(MadeFromCord(Green), Size.Medium, Size.Small),
        ComplexStitch(MadeFromCord(Blue), Size.Large, Size.Medium),
    )
    val binding = CopticBinding(cover, sewingPattern = ComplexSewingPattern(stitches))

    val book = Book(
        binding,
        size = Size2d.fromMillimeters(125, 190)
    )

    val svg = visualizeTextFormat(State(), TEXT_CONFIG, book)

    File("book-sewing-patterns-complex.svg").writeText(svg.export())
}