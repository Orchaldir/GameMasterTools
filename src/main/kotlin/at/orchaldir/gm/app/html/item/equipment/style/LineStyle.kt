package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.ORNAMENT
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
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
            is OrnamentLine -> {
                showOrnament(call, state, line.ornament)
                field("Size", line.size)
            }
            is Rope -> showThicknessAndPart(call, state, line.thickness, line.main)
            is Wire -> showThicknessAndPart(call, state, line.thickness, line.main)
        }
    }
}

private fun DETAILS.showThicknessAndPart(
    call: ApplicationCall,
    state: State,
    thickness: Size,
    part: ColorSchemeItemPart,
) {
    field("Thickness", thickness)
    showColorSchemeItemPart(call, state, part, "Main")
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
            is Chain -> selectThicknessAndPart(state, param, line.thickness, line.main)
            is OrnamentLine -> {
                editOrnament(state, line.ornament, param = combine(param, ORNAMENT))
                selectValue("Size", combine(param, SIZE), Size.entries, line.size)
            }
            is Rope -> selectThicknessAndPart(state, param, line.thickness, line.main)
            is Wire -> selectThicknessAndPart(state, param, line.thickness, line.main)
        }
    }
}

private fun DETAILS.selectThicknessAndPart(
    state: State,
    param: String,
    thickness: Size,
    part: ColorSchemeItemPart,
) {
    selectValue("Thickness", combine(param, SIZE), Size.entries, thickness)
    editColorSchemeItemPart(state, part, combine(param, MAIN), "Main")
}

// parse

fun parseLineStyle(parameters: Parameters, param: String): LineStyle {
    val type = parse(parameters, combine(param, STYLE), LineStyleType.Wire)

    return when (type) {
        LineStyleType.Chain -> Chain(
            parseThickness(parameters, param),
            parseItemPart(parameters, param),
        )

        LineStyleType.Ornament -> OrnamentLine(
            parseOrnament(parameters, combine(param, ORNAMENT)),
            parseThickness(parameters, param),
        )

        LineStyleType.Rope -> Rope(
            parseItemPart(parameters, param),
            parseThickness(parameters, param),
        )

        LineStyleType.Wire -> Wire(
            parseThickness(parameters, param),
            parseItemPart(parameters, param),
        )
    }
}

private fun parseItemPart(parameters: Parameters, param: String, ) =
    parseColorSchemeItemPart(parameters, combine(param, MAIN))

private fun parseThickness(parameters: Parameters, param: String) =
    parse(parameters, combine(param, SIZE), Size.Medium)

