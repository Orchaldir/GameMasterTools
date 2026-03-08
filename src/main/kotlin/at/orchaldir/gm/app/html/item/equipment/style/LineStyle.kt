package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.ORNAMENT
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseMadeFromCord
import at.orchaldir.gm.app.html.util.part.parseMadeFromMetal
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.ItemPartType
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showLineStyle(
    call: ApplicationCall,
    state: State,
    line: LineStyle,
    label: String,
) {
    showDetails(label) {
        field("Style", line.getType())

        when (line) {
            is Chain -> showThicknessAndPart(call, state, line.thickness, line.main)
            is Cord -> showThicknessAndPart(call, state, line.thickness, line.main)
            is OrnamentLine -> {
                showOrnament(call, state, line.ornament)
                field("Size", line.size)
            }

            is Wire -> showThicknessAndPart(call, state, line.thickness, line.main)
        }
    }
}

private fun DETAILS.showThicknessAndPart(
    call: ApplicationCall,
    state: State,
    thickness: Size,
    part: ItemPart,
) {
    field("Thickness", thickness)
    showItemPart(call, state, part)
}

// edit

fun HtmlBlockTag.editLineStyle(
    state: State,
    line: LineStyle,
    label: String,
    param: String,
) {
    showDetails(label, true) {
        selectValue("Style", combine(param, STYLE), LineStyleType.entries, line.getType())

        when (line) {
            is Chain -> selectThicknessAndPart(state, param, line.thickness, line.main, ItemPartType.Metal)
            is Cord -> selectThicknessAndPart(state, param, line.thickness, line.main, ItemPartType.Cord)
            is OrnamentLine -> {
                editOrnament(state, line.ornament, param = combine(param, ORNAMENT))
                selectValue("Size", combine(param, SIZE), Size.entries, line.size)
            }

            is Wire -> selectThicknessAndPart(state, param, line.thickness, line.main, ItemPartType.Metal)
        }
    }
}

private fun DETAILS.selectThicknessAndPart(
    state: State,
    param: String,
    thickness: Size,
    part: ItemPart,
    allowedType: ItemPartType,
) {
    selectValue("Thickness", combine(param, SIZE), Size.entries, thickness)
    editItemPart(state, part, combine(param, MAIN), allowedTypes = setOf(allowedType))
}

// parse

fun parseLineStyle(parameters: Parameters, param: String): LineStyle {
    val type = parse(parameters, combine(param, STYLE), LineStyleType.Wire)

    return when (type) {
        LineStyleType.Chain -> Chain(
            parseThickness(parameters, param),
            parseMadeFromMetal(parameters, combine(param, MAIN)),
        )

        LineStyleType.Cord -> Cord(
            parseMadeFromCord(parameters, combine(param, MAIN)),
            parseThickness(parameters, param),
        )

        LineStyleType.Ornament -> OrnamentLine(
            parseOrnament(parameters, combine(param, ORNAMENT)),
            parseThickness(parameters, param),
        )

        LineStyleType.Wire -> Wire(
            parseThickness(parameters, param),
            parseMadeFromMetal(parameters, combine(param, MAIN)),
        )
    }
}

private fun parseThickness(parameters: Parameters, param: String) =
    parse(parameters, combine(param, SIZE), Size.Medium)

