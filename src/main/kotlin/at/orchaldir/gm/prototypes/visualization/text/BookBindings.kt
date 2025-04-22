package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

private val ID = MaterialId(0)

fun main() {
    val size = Size2i.fromMillimeters(125, 190)

    renderTextTable(
        "book-bindings.svg",
        State(),
        TEXT_CONFIG,
        size.toSize2d() + fromMillimeters(50),
        addNames(listOf(Color.Blue, Color.Red, Color.Black, Color.Green)),
        addNames(BookBindingType.entries),
    ) { color, type ->
        val cover = FillItemPart(color)
        Book(
            100,
            when (type) {
                BookBindingType.Coptic -> CopticBinding(cover, sewingPattern = SimpleSewingPattern(Color.White))
                BookBindingType.Hardcover -> Hardcover(cover)
                BookBindingType.Leather -> LeatherBinding(Color.SaddleBrown, ID, LeatherBindingStyle.Half, cover)
            },
            size
        )
    }
}