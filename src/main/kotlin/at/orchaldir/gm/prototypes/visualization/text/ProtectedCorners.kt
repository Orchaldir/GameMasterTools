package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2i

private val ID = MaterialId(0)

fun main() {
    val bookSize = Size2i(125, 190)

    renderTextTable(
        "book-protect-corners.svg",
        TEXT_CONFIG,
        bookSize.toSize2d() + Distance(50),
        addNames(CornerShape.entries),
        addNames(Size.entries),
    ) { shape, size ->
        val cover = BookCover(Color.Green, ID)
        Book(
            100,
            Hardcover(
                cover, protection = ProtectedCorners(
                    shape,
                    size,
                )
            ),
            bookSize
        )
    }
}