package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.font.SolidFont
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.BookCover
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.typography.SimpleTypography
import at.orchaldir.gm.core.model.item.text.book.typography.TypographyLayout
import at.orchaldir.gm.core.model.item.text.book.typography.TypographyOrder
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.visualization.text.ResolvedTextData

fun main() {
    val size = Size2i(125, 190)

    renderTextTable(
        "book-typographies-simple.svg",
        TEXT_CONFIG,
        size.toSize2d() + fromMillimeters(50),
        addNames(TypographyLayout.entries),
        addNames(TypographyOrder.entries),
        ResolvedTextData("The Shadow over Innsmouth", "H. P. Lovecraft"),
    ) { layout, order ->
        Book(
            100,
            Hardcover(
                BookCover(
                    typography = SimpleTypography(
                        SolidFont(fromMillimeters(10)),
                        SolidFont(fromMillimeters(15)),
                        order,
                        layout,
                    )
                )
            ),
            size,
        )
    }
}