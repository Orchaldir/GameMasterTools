package at.orchaldir.gm.prototypes.visualization.book

import at.orchaldir.gm.core.model.item.book.BookCover
import at.orchaldir.gm.core.model.item.book.Codex
import at.orchaldir.gm.core.model.item.book.LeatherBinding
import at.orchaldir.gm.core.model.item.book.LeatherBindingType
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2i

private val ID = MaterialId(0)

fun main() {
    val size = Size2i(125, 190)

    renderBookTable(
        "book-leather-bindings.svg",
        BOOK_CONFIG,
        size.toSize2d() + Distance(50),
        addNames(listOf(Color.Blue, Color.Red, Color.Black, Color.Green)),
        addNames(LeatherBindingType.entries),
    ) { color, type ->
        val cover = BookCover(color, ID)
        Codex(
            100,
            LeatherBinding(Color.SaddleBrown, ID, type, cover),
            size
        )
    }
}