package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.TextFormat
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.book.typography.AdvancedTypography
import at.orchaldir.gm.core.model.item.text.book.typography.SimpleStringRenderOption
import at.orchaldir.gm.core.model.item.text.book.typography.StringRenderOption
import at.orchaldir.gm.core.model.item.text.book.typography.WrappedStringRenderOption
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Orientation
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.visualization.text.ResolvedTextData

private val topAuthor = SimpleStringRenderOption(
    Distance(100),
    Distance(50),
    SolidFont(Color.Aqua, Distance(20)),
)

private val bottomAuthor = SimpleStringRenderOption(
    Distance(100),
    Distance(250),
    SolidFont(Color.Red, Distance(20)),
)

fun main() {
    val bookSize = Size2i(200, 300)
    val texts = listOf(
        createRow(bookSize) { size -> SolidFont(Color.White, size) },
        createRow(bookSize) { size -> FontWithBorder(Color.Gold, Color.Black, size, Distance(2)) },
        listOf(
            createWrappedTitle(bookSize, ResolvedTextData("Long Title")),
            createWrappedTitle(bookSize, ResolvedTextData("Very Long Title"))
        )
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
        SimpleStringRenderOption(
            Distance(100),
            Distance(150),
            createFont(Distance(80)),
        ),
        topAuthor,
        ResolvedTextData("Title", "Max Musterman"),
    ),
    createTypography(
        bookSize,
        SimpleStringRenderOption(
            Distance(100),
            Distance(60),
            createFont(Distance(40)),
        ),
        bottomAuthor,
        ResolvedTextData("Long Title", "Max Musterman"),
    ),
    createTypography(
        bookSize,
        SimpleStringRenderOption(
            Distance(100),
            Distance(150),
            createFont(Distance(80)),
            Orientation.fromDegree(20.0f)
        ),
        bottomAuthor,
        ResolvedTextData("Title"),
    ),
)

private fun createWrappedTitle(bookSize: Size2i, data: ResolvedTextData) = createTypography(
    bookSize,
    WrappedStringRenderOption(
        Distance(100),
        Distance(150),
        SolidFont(Color.Black, Distance(80)),
        Distance(180),
    ),
    topAuthor,
    data,
)

private fun createTypography(
    size: Size2i,
    titleOption: StringRenderOption,
    authorOption: StringRenderOption,
    data: ResolvedTextData,
): Pair<TextFormat, ResolvedTextData> = Pair(
    Book(
    100,
    Hardcover(
        BookCover(
            typography = AdvancedTypography(titleOption, authorOption)
        )
    ),
    size,
    ),
    data
)