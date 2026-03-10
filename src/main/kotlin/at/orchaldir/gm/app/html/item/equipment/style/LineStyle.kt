package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.ORNAMENT
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.ItemPartType
import at.orchaldir.gm.core.model.util.part.MADE_FROM_METALS
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
    allowedTypes: Collection<LineStyleType> = LineStyleType.entries,
) {
    showDetails(label, true) {
        selectValue(
            "Style",
            combine(param, STYLE),
            allowedTypes,
            line.getType(),
        )

        when (line) {
            is Chain -> selectThicknessAndPart(state, param, line.thickness, line.main, MADE_FROM_METALS)
            is Cord -> selectThicknessAndPart(state, param, line.thickness, line.main, CLOTHING_MATERIALS)
            is OrnamentLine -> {
                editOrnament(state, line.ornament, param = combine(param, ORNAMENT))
                selectValue("Size", combine(param, SIZE), Size.entries, line.size)
            }

            is Wire -> selectThicknessAndPart(state, param, line.thickness, line.main, MADE_FROM_METALS)
        }
    }
}

private fun DETAILS.selectThicknessAndPart(
    state: State,
    param: String,
    thickness: Size,
    part: ItemPart,
    allowedTypes: Collection<ItemPartType>,
) {
    selectValue("Thickness", combine(param, SIZE), Size.entries, thickness)
    editItemPart(state, part, combine(param, MAIN), allowedTypes = allowedTypes)
}

// parse

fun parseLineStyle(parameters: Parameters, param: String): LineStyle {
    val type = parse(parameters, combine(param, STYLE), LineStyleType.Wire)

    return when (type) {
        LineStyleType.Chain -> Chain(
            parseThickness(parameters, param),
            parseItemPart(parameters, combine(param, MAIN), MADE_FROM_METALS),
        )

        LineStyleType.Cord -> Cord(
            parseItemPart(parameters, combine(param, MAIN), CLOTHING_MATERIALS),
            parseThickness(parameters, param),
        )

        LineStyleType.Ornament -> OrnamentLine(
            parseOrnament(parameters, combine(param, ORNAMENT)),
            parseThickness(parameters, param),
        )

        LineStyleType.Wire -> parseWire(parameters, param)
    }
}

fun parseWire(
    parameters: Parameters,
    param: String,
): Wire = Wire(
    parseThickness(parameters, param),
    parseItemPart(parameters, combine(param, MAIN), MADE_FROM_METALS),
)

private fun parseThickness(parameters: Parameters, param: String) =
    parse(parameters, combine(param, SIZE), Size.Medium)

