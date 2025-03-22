package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.BookCover
import at.orchaldir.gm.core.model.item.text.book.LeatherBinding
import at.orchaldir.gm.core.model.item.text.book.LeatherBindingType
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

private val ID = MaterialId(0)

fun main() {
    val size = Size2i.fromMillimeters(125, 190)

    renderTextTable(
        "book-leather-bindings.svg",
        TEXT_CONFIG,
        size.toSize2d() + fromMillimeters(50),
        addNames(listOf(Color.Blue, Color.Red, Color.Black, Color.Green)),
        addNames(LeatherBindingType.entries),
    ) { color, type ->
        val cover = BookCover(color, ID)
        Book(
            100,
            LeatherBinding(Color.SaddleBrown, ID, type, cover),
            size
        )
    }
}