package at.orchaldir.gm.prototypes.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.font.SolidFont
import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.text.TEXT_CONFIG
import at.orchaldir.gm.prototypes.visualization.text.renderTextContentTable
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

fun main() {
    val book = Book(
        Hardcover(),
        page = ColorItemPart(Color.AntiqueWhite),
        size = Size2d.fromMillimeters(125, 190)
    )
    val font = SolidFont(
        fromMillimeters(20),
        Color.Red,
    )
    val initial0 = LargeInitials(fromPercentage(200))
    val initial1 = LargeInitials(fromPercentage(300))
    val initial2 = FontInitials(font)
    val initials = listOf(
        Pair("200%", initial0),
        Pair("300%", initial1),
        Pair("Font", initial2),
    )

    renderTextContentTable(
        "book-initials.svg",
        State(),
        TEXT_CONFIG,
        book,
        addNames(InitialPosition.entries),
        initials,
    ) { position, initial ->
        val update = when (initial) {
            NormalInitials -> initial
            is FontInitials -> initial.copy(position = position)
            is LargeInitials -> initial.copy(position = position)
        }
        AbstractText(
            style = ContentStyle(initials = update),
        )
    }
}