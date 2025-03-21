package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.BookCover
import at.orchaldir.gm.core.model.item.text.book.CornerShape
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.ProtectedCorners
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

private val ID = MaterialId(0)

fun main() {
    val bookSize = Size2i.fromMillimeters(125, 190)

    renderTextTable(
        "book-protect-corners.svg",
        TEXT_CONFIG,
        bookSize.toSize2d() + fromMillimeters(50),
        addNames(listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f)),
        addNames(CornerShape.entries),
    ) { size, shape ->
        val cover = BookCover(Color.Green, ID)
        val protection = ProtectedCorners(shape, Factor(size))

        Book(
            100,
            Hardcover(cover, protection = protection),
            bookSize
        )
    }
}