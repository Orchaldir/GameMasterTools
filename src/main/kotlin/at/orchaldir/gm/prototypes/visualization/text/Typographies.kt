package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.TextFormat
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Orientation
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.visualization.text.ResolvedTextData

fun main() {
    val bookSize = Size2i(200, 300)
    val texts = listOf(
        createRow(bookSize) { size -> SolidFont(Color.White, size) },
        createRow(bookSize) { size -> FontWithBorder(Color.Gold, Color.Black, size, Distance(2)) },
    )

    renderResolvedTextTable(
        "book-typographies.svg",
        TEXT_CONFIG,
        texts,
    )
}

private fun createRow(
    bookSize: Size2i,
    createFont: (Distance) -> FontOption,
) = listOf(
    createTypography(
        bookSize,
        SimpleTextRenderOption(
            Distance(100),
            Distance(150),
            createFont(Distance(80)),
        ),
        ResolvedTextData("Title"),
    ),
    createTypography(
        bookSize,
        SimpleTextRenderOption(
            Distance(100),
            Distance(60),
            createFont(Distance(40)),
        ),
        ResolvedTextData("Long Title"),
    ),
    createTypography(
        bookSize,
        SimpleTextRenderOption(
            Distance(100),
            Distance(100),
            createFont(Distance(80)),
            Orientation.fromDegree(20.0f)
        ),
        ResolvedTextData("Title"),
    ),
)

private fun createTypography(
    size: Size2i,
    option: TextRenderOption,
    data: ResolvedTextData,
): Pair<TextFormat, ResolvedTextData> = Pair(
    Book(
    100,
    Hardcover(
        BookCover(
            typography = AdvancedTypography(option)
        )
    ),
    size,
    ),
    data
)