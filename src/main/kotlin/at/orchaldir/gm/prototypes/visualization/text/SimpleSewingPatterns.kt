package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.CopticBinding
import at.orchaldir.gm.core.model.item.text.book.SimpleSewingPattern
import at.orchaldir.gm.core.model.item.text.book.StitchType.Empty
import at.orchaldir.gm.core.model.item.text.book.StitchType.Kettle
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

fun main() {
    val size = Size2d.fromMillimeters(125, 190)

    renderTextFormatTable(
        "book-sewing-patterns-simple.svg",
        State(),
        TEXT_CONFIG,
        size + fromMillimeters(50),
        listOf(
            Pair("Small + Short", Pair(Size.Small, Size.Small)),
            Pair("Small + Medium", Pair(Size.Small, Size.Medium)),
            Pair("Small + Long", Pair(Size.Small, Size.Large)),
            Pair("Medium + Short", Pair(Size.Medium, Size.Small)),
            Pair("Medium + Medium", Pair(Size.Medium, Size.Medium)),
            Pair("Medium + Long", Pair(Size.Medium, Size.Large)),
            Pair("Large + Short", Pair(Size.Large, Size.Small)),
            Pair("Large + Medium", Pair(Size.Large, Size.Medium)),
            Pair("Large + Long", Pair(Size.Large, Size.Large)),
        ),
        listOf(
            Pair("1", listOf(Kettle, Kettle, Kettle, Kettle)),
            Pair("2", listOf(Kettle, Empty, Kettle)),
            Pair("3", listOf(Empty, Kettle, Kettle, Empty, Empty, Empty, Empty, Kettle, Kettle, Empty)),
        ),
    ) { (sewingSize, sewingLength), pattern ->
        val sewingPattern = SimpleSewingPattern(ColorItemPart(Color.Red), sewingSize, sewingLength, pattern)
        val binding = CopticBinding(FillItemPart(Color.SaddleBrown), sewingPattern = sewingPattern)

        Book(binding, size = size)
    }
}