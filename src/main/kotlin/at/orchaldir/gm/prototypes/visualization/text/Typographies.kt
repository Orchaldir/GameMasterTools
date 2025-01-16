package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2i

fun main() {
    val bookSize = Size2i(200, 300)
    val texts = listOf(
        listOf(
            createTypography(
                bookSize,
                SimpleTextRenderOption(
                    Distance(100),
                    Distance(150),
                    Distance(80),
                )
            ),
            createTypography(
                bookSize,
                SimpleTextRenderOption(
                    Distance(100),
                    Distance(60),
                    Distance(40),
                )
            ),
        )
    )

    renderTextTable(
        "book-typographies.svg",
        TEXT_CONFIG,
        texts,
    )
}

private fun createTypography(size: Size2i, option: TextRenderOption) = Book(
    100,
    Hardcover(
        BookCover(
            typography = SimpleTypography(option)
        )
    ),
    size,
)