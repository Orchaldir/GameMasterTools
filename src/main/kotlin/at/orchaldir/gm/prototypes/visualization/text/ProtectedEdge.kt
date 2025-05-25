package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.ProtectedEdge
import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.FillItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

fun main() {
    val bookSize = Size2d.fromMillimeters(125, 190)

    renderTextFormatTable(
        "book-protect-edges.svg",
        State(),
        TEXT_CONFIG,
        bookSize + fromMillimeters(50),
        addNames(listOf(5, 10, 15, 20)),
        addNames(listOf(Color.Gray, Color.Gold)),
    ) { size, color ->
        val protection = ProtectedEdge(fromPercentage(size), ColorItemPart(color))
        val binding = Hardcover(FillItemPart(Color.Green), protection = protection)

        Book(binding, size = bookSize)
    }
}