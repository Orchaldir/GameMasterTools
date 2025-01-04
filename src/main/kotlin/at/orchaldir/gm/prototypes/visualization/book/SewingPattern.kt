package at.orchaldir.gm.prototypes.visualization.book

import at.orchaldir.gm.core.model.item.book.*
import at.orchaldir.gm.core.model.item.book.StitchType.FrenchLink
import at.orchaldir.gm.core.model.item.book.StitchType.Kettle
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2i

private val ID = MaterialId(0)

fun main() {
    val size = Size2i(125, 190)

    renderBookTable(
        "book-simple-sewing-patterns.svg",
        BOOK_CONFIG,
        size.toSize2d() + Distance(50),
        listOf(
            Pair("1", listOf(Kettle, Kettle, Kettle, Kettle)),
            Pair("2", listOf(Kettle, FrenchLink, FrenchLink, Kettle)),
        ),
        addNames(Size.entries),
    ) { pattern, sewingSize ->
        val cover = BookCover(Color.SaddleBrown, ID)
        Codex(
            100,
            CopticBinding(cover, SimpleSewingPattern(Color.Red, sewingSize, pattern)),
            size
        )
    }
}