package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.typography.SimpleTypography
import at.orchaldir.gm.core.model.item.text.book.typography.TypographyLayout
import at.orchaldir.gm.core.model.item.text.book.typography.TypographyOrder
import at.orchaldir.gm.core.model.util.font.SolidFont
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.visualization.text.ResolvedTextData

fun main() {
    val size = Size2d.fromMillimeters(125, 190)

    renderTextFormatTable(
        "book-typographies-simple.svg",
        State(),
        TEXT_CONFIG,
        size + fromMillimeters(50),
        addNames(TypographyLayout.entries),
        addNames(TypographyOrder.entries),
        ResolvedTextData("The Shadow over Innsmouth", "H. P. Lovecraft"),
    ) { layout, order ->
        val typography = SimpleTypography(
            SolidFont(fromMillimeters(10)),
            SolidFont(fromMillimeters(15)),
            order,
            layout,
        )
        val binding = Hardcover(typography = typography)

        Book(binding, size = size)
    }
}