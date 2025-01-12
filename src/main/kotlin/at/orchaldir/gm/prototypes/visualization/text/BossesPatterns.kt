package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2i

private val ID = MaterialId(0)

fun main() {
    val bookSize = Size2i(125, 190)

    renderTextTable(
        "book-bosses-patterns.svg",
        TEXT_CONFIG,
        bookSize.toSize2d() + Distance(50),
        listOf(
            Pair("Circle + Short", Pair(BossesShape.Circle, Size.Small)),
            Pair("Circle + Medium", Pair(BossesShape.Circle, Size.Medium)),
            Pair("Circle + Long", Pair(BossesShape.Circle, Size.Large)),
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
        val cover = BookCover(Color.SaddleBrown, ID)
        Book(
            100,
            Hardcover(cover, SimpleBossesPattern(pattern, shape, size)),
            bookSize
        )
    }
}