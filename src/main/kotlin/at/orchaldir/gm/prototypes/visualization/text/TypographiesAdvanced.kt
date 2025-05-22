package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.TextFormat
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.typography.AdvancedTypography
import at.orchaldir.gm.core.model.item.text.book.typography.SimpleStringRenderOption
import at.orchaldir.gm.core.model.item.text.book.typography.StringRenderOption
import at.orchaldir.gm.core.model.item.text.book.typography.WrappedStringRenderOption
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.font.FontOption
import at.orchaldir.gm.core.model.util.font.FontWithBorder
import at.orchaldir.gm.core.model.util.font.SolidFont
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import at.orchaldir.gm.visualization.text.ResolvedTextData

private val topAuthor = SimpleStringRenderOption(
    fromMillimeters(100),
    fromMillimeters(50),
    SolidFont(fromMillimeters(20), Color.Aqua),
)

private val bottomAuthor = SimpleStringRenderOption(
    fromMillimeters(100),
    fromMillimeters(250),
    SolidFont(fromMillimeters(20), Color.Red),
)

fun main() {
    val bookSize = Size2d.fromMillimeters(200, 300)
    val texts = listOf(
        createRow(bookSize) { size -> SolidFont(size, Color.White) },
        createRow(bookSize) { size -> FontWithBorder(size, fromMillimeters(2), Color.Gold, Color.Black) },
        listOf(
            createWrappedTitle(bookSize, ResolvedTextData("Long Title")),
            createWrappedTitle(bookSize, ResolvedTextData("Very Long Title"))
        )
    )

    renderResolvedTextTable(
        "book-typographies-advanced.svg",
        State(),
        TEXT_CONFIG,
        texts,
    )
}

private fun createRow(
    bookSize: Size2d,
    createFont: (Distance) -> FontOption,
) = listOf(
    createTypography(
        bookSize,
        SimpleStringRenderOption(
            fromMillimeters(100),
            fromMillimeters(150),
            createFont(fromMillimeters(80)),
        ),
        topAuthor,
        ResolvedTextData("Title", "Max Musterman"),
    ),
    createTypography(
        bookSize,
        SimpleStringRenderOption(
            fromMillimeters(100),
            fromMillimeters(60),
            createFont(fromMillimeters(40)),
        ),
        bottomAuthor,
        ResolvedTextData("Long Title", "Max Musterman"),
    ),
    createTypography(
        bookSize,
        SimpleStringRenderOption(
            fromMillimeters(100),
            fromMillimeters(150),
            createFont(fromMillimeters(80)),
            fromDegrees(20)
        ),
        bottomAuthor,
        ResolvedTextData("Title"),
    ),
)

private fun createWrappedTitle(bookSize: Size2d, data: ResolvedTextData) = createTypography(
    bookSize,
    WrappedStringRenderOption(
        fromMillimeters(100),
        fromMillimeters(150),
        SolidFont(fromMillimeters(80), Color.Black),
        fromMillimeters(180),
    ),
    topAuthor,
    data,
)

private fun createTypography(
    size: Size2d,
    titleOption: StringRenderOption,
    authorOption: StringRenderOption,
    data: ResolvedTextData,
): Pair<TextFormat, ResolvedTextData> {
    val binding = Hardcover(typography = AdvancedTypography(titleOption, authorOption))
    val book = Book(binding, size = size)

    return Pair(book, data)
}