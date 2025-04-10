package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.LINE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Necklace
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showNecklace(
    call: ApplicationCall,
    state: State,
    necklace: Necklace,
) {
    field("Style", necklace.style.getType())

    when (val style = necklace.style) {
        is DangleNecklace -> {
            showDangleEarring(call, state, style.dangle)
            showJewelryLine(call, state, style.line, "Line")
        }

        is DropNecklace -> {
            showDropEarring(call, state, style.drop)
            showJewelryLine(call, state, style.line, "Line")
        }

        is PendantNecklace -> {
            showOrnament(call, state, style.ornament)
            field("Size", style.size)
            showJewelryLine(call, state, style.line, "Line")
        }

        is StrandNecklace -> {
            field("Number of Strands", style.strands)
            showJewelryLine(call, state, style.line, "Strands")
            field("Padding between Strands", style.padding)
        }
    }
}

// edit

fun FORM.editNecklace(
    state: State,
    necklace: Necklace,
) {
    selectValue("Style", STYLE, NecklaceStyleType.entries, necklace.style.getType(), true)

    when (val style = necklace.style) {
        is DangleNecklace -> {
            editDangleEarring(state, style.dangle)
            editJewelryLine(state, style.line, "Line", LINE)
        }

        is DropNecklace -> {
            editDropEarring(state, style.drop)
            editJewelryLine(state, style.line, "Line", LINE)
        }

        is PendantNecklace -> {
            editOrnament(state, style.ornament)
            selectValue("Size", SIZE, Size.entries, style.size, true)
            editJewelryLine(state, style.line, "Line", LINE)
        }

        is StrandNecklace -> {
            selectInt("Number of Strands", style.strands, 1, 3, 1, NUMBER, true)
            editJewelryLine(state, style.line, "Strands", LINE)
            selectValue("Padding between Strands", SIZE, Size.entries, style.padding, true)
        }
    }
}

// parse

fun parseNecklace(parameters: Parameters): Necklace {
    val type = parse(parameters, STYLE, NecklaceStyleType.Pendant)

    return Necklace(
        when (type) {
            NecklaceStyleType.Dangle -> DangleNecklace(
                parseDangleEarring(parameters),
                parseJewelryLine(parameters, LINE),
            )

            NecklaceStyleType.Drop -> DropNecklace(
                parseDropEarring(parameters),
                parseJewelryLine(parameters, LINE),
            )

            NecklaceStyleType.Pendant -> PendantNecklace(
                parseOrnament(parameters),
                parseJewelryLine(parameters, LINE),
                parse(parameters, SIZE, Size.Medium),
            )

            NecklaceStyleType.Strand -> StrandNecklace(
                parseInt(parameters, NUMBER),
                parseJewelryLine(parameters, LINE),
                parse(parameters, SIZE, Size.Medium),
            )
        }
    )
}

