package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
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
    val leather = ColorItemPart(Color.SaddleBrown)
    val sewingPattern = SimpleSewingPattern(ColorItemPart(Color.White))

    renderTextFormatTable(
        "book-bindings.svg",
        State(),
        TEXT_CONFIG,
        size.toSize2d() + fromMillimeters(50),
        addNames(listOf(Color.Blue, Color.Red, Color.Black, Color.Green)),
        addNames(BookBindingType.entries),
    ) { color, type ->
        val cover = FillItemPart(color)
        val binding = when (type) {
            BookBindingType.Coptic -> CopticBinding(cover, sewingPattern = sewingPattern)
            BookBindingType.Hardcover -> Hardcover(cover)
            BookBindingType.Leather -> LeatherBinding(LeatherBindingStyle.Half, cover, leather)
        }

        Book(binding, size = size)
    }
}