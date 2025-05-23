package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.CornerShape
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.ProtectedCorners
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

fun main() {
    val bookSize = Size2d.fromMillimeters(125, 190)

    renderTextFormatTable(
        "book-protect-corners.svg",
        State(),
        TEXT_CONFIG,
        bookSize + fromMillimeters(50),
        addNames(listOf(10, 20, 30, 40, 50)),
        addNames(CornerShape.entries),
    ) { size, shape ->
        val protection = ProtectedCorners(shape, fromPercentage(size), ColorItemPart(Color.Silver))
        val binding = Hardcover(FillItemPart(Color.Green), protection = protection)

        Book(binding, size = bookSize)
    }
}