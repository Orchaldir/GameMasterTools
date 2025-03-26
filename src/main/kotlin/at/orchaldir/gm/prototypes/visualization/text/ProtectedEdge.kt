package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.BookCover
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.ProtectedEdge
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

private val ID = MaterialId(0)

fun main() {
    val bookSize = Size2i.fromMillimeters(125, 190)

    renderTextTable(
        "book-protect-edges.svg",
        TEXT_CONFIG,
        bookSize.toSize2d() + fromMillimeters(50),
        addNames(listOf(5, 10, 15, 20)),
        addNames(listOf(Color.Gray, Color.Gold)),
    ) { size, color ->
        val cover = BookCover(Color.Green, ID)
        val protection = ProtectedEdge(fromPercentage(size), color)

        Book(
            100,
            Hardcover(cover, protection = protection),
            bookSize
        )
    }
}