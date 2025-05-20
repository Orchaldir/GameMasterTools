package at.orchaldir.gm.prototypes.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.font.SolidFont
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.typography.SimpleTitleTypography
import at.orchaldir.gm.core.model.item.text.book.typography.TypographyLayout
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import at.orchaldir.gm.visualization.text.ResolvedTextData

fun main() {
    val size = Size2d.fromMillimeters(125, 190)

    renderTextFormatTable(
        "book-typographies-title.svg",
        State(),
        TEXT_CONFIG,
        size + fromMillimeters(50),
        addNames(TypographyLayout.getTitleEntries()),
        listOf(
            Pair("Small", fromMillimeters(10)),
            Pair("Medium", fromMillimeters(15)),
            Pair("Large", fromMillimeters(20)),
        ),
        ResolvedTextData("The Colour Out of Space"),
    ) { layout, fontSize ->
        val typography = SimpleTitleTypography(
            SolidFont(fontSize, Color.Red),
            layout,
        )
        val binding = Hardcover(
            FillItemPart(Color.Black),
            typography = typography
        )

        Book(binding, size = size)
    }
}