package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.book.typography.SimpleTypography
import at.orchaldir.gm.core.model.item.text.book.typography.TypographyLayout
import at.orchaldir.gm.core.model.item.text.book.typography.TypographyOrder
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2i

fun main() {
    val size = Size2i(125, 190)

    renderTextTable(
        "book-typographies-simple.svg",
        TEXT_CONFIG,
        size.toSize2d() + Distance(50),
        addNames(TypographyLayout.entries),
        addNames(TypographyOrder.entries),
    ) { layout, order ->
        Book(
            100,
            Hardcover(
                BookCover(
                    typography = SimpleTypography(
                        SolidFont(Color.White, Distance(20)),
                        SolidFont(Color.White, Distance(40)),
                        order,
                        layout,
                    )
                )
            ),
            size,
        )
    }
}