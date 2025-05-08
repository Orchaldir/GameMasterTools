package at.orchaldir.gm.prototypes.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.FontWithBorder
import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.prototypes.visualization.addNames
import at.orchaldir.gm.prototypes.visualization.text.TEXT_CONFIG
import at.orchaldir.gm.prototypes.visualization.text.renderTextContentTable
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMicrometers
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters

fun main() {
    val book = Book(
        Hardcover(),
        page = ColorItemPart(Color.AntiqueWhite),
        size = Size2i.Companion.fromMillimeters(125, 190)
    )
    val font = FontWithBorder(
        fromMillimeters(15),
        fromMicrometers(100),
        Color.Gold,
        Color.Red,
    )
    val initial0 = LargeInitial(fromPercentage(200))
    val initial1 = LargeInitial(fromPercentage(300))
    val initial2 = FontInitial(font)
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
            NormalInitial -> initial
            is FontInitial -> initial.copy(position = position)
            is LargeInitial -> initial.copy(position = position)
        }
        AbstractText(
            style = ContentStyle(initial = update),
        )
    }
}