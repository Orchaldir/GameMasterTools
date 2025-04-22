package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.BookCover
import at.orchaldir.gm.core.model.item.text.book.CopticBinding
import at.orchaldir.gm.core.model.item.text.book.SimpleSewingPattern
import at.orchaldir.gm.core.model.item.text.book.StitchType.Empty
import at.orchaldir.gm.core.model.item.text.book.StitchType.Kettle
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

private val ID = MaterialId(0)

fun main() {
    val size = Size2i.fromMillimeters(125, 190)

    renderTextTable(
        "book-sewing-patterns-simple.svg",
        State(),
        TEXT_CONFIG,
        size.toSize2d() + fromMillimeters(50),
        listOf(
            Pair("Small + Short", Pair(Size.Small, Size.Small)),
            Pair("Small + Medium", Pair(Size.Small, Size.Medium)),
            Pair("Small + Long", Pair(Size.Small, Size.Large)),
            Pair("Medium + Short", Pair(Size.Medium, Size.Small)),
            Pair("Medium + Medium", Pair(Size.Medium, Size.Medium)),
            Pair("Medium + Long", Pair(Size.Medium, Size.Large)),
            Pair("Large + Short", Pair(Size.Large, Size.Small)),
            Pair("Large + Medium", Pair(Size.Large, Size.Medium)),
            Pair("Large + Long", Pair(Size.Large, Size.Large)),
        ),
        listOf(
            Pair("1", listOf(Kettle, Kettle, Kettle, Kettle)),
            Pair("2", listOf(Kettle, Empty, Kettle)),
            Pair("3", listOf(Empty, Kettle, Kettle, Empty, Empty, Empty, Empty, Kettle, Kettle, Empty)),
        ),
    ) { (sewingSize, sewingLength), pattern ->
        val cover = BookCover(FillItemPart(Color.SaddleBrown))
        Book(
            100,
            CopticBinding(cover, SimpleSewingPattern(Color.Red, sewingSize, sewingLength, pattern)),
            size
        )
    }
}