package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.BookCover
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.typography.SimpleTitleTypography
import at.orchaldir.gm.core.model.item.text.book.typography.TypographyLayout
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.font.SolidFont
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.visualization.text.ResolvedTextData

fun main() {
    val size = Size2i(125, 190)

    renderTextTable(
        "book-typographies-title.svg",
        TEXT_CONFIG,
        size.toSize2d() + Distance(50),
        addNames(TypographyLayout.getTitleEntries()),
        listOf(
            Pair("Small", Distance(10)),
            Pair("Medium", Distance(15)),
            Pair("Large", Distance(20)),
        ),
        ResolvedTextData("The Colour Out of Space"),
    ) { layout, fontSize ->
        Book(
            100,
            Hardcover(
                BookCover(
                    Color.Black,
                    typography = SimpleTitleTypography(
                        SolidFont(fontSize, Color.Red),
                        layout,
                    )
                )
            ),
            size,
        )
    }
}