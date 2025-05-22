package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.BossesShape
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.SimpleBossesPattern
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

fun main() {
    val bookSize = Size2d.fromMillimeters(125, 190)

    renderTextFormatTable(
        "book-bosses-patterns.svg",
        State(),
        TEXT_CONFIG,
        bookSize + fromMillimeters(50),
        listOf(
            Pair("Circle + Short", Pair(BossesShape.Circle, Size.Small)),
            Pair("Circle + Medium", Pair(BossesShape.Circle, Size.Medium)),
            Pair("Circle + Long", Pair(BossesShape.Circle, Size.Large)),
            Pair("Diamond + Short", Pair(BossesShape.Diamond, Size.Small)),
            Pair("Diamond + Medium", Pair(BossesShape.Diamond, Size.Medium)),
            Pair("Diamond + Long", Pair(BossesShape.Diamond, Size.Large)),
            Pair("Square + Short", Pair(BossesShape.Square, Size.Small)),
            Pair("Square + Medium", Pair(BossesShape.Square, Size.Medium)),
            Pair("Square + Long", Pair(BossesShape.Square, Size.Large)),
        ),
        listOf(
            Pair("1", listOf(2, 1, 2)),
            Pair("2", listOf(2, 2)),
            Pair("3", listOf(1)),
        ),
    ) { (shape, size), pattern ->
        val cover = FillItemPart(Color.SaddleBrown)
        val bosses = SimpleBossesPattern(pattern, shape, size, ColorItemPart(Color.Gray))
        val binding = Hardcover(cover, bosses = bosses)

        Book(binding, size = bookSize)
    }
}