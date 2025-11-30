package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.ORNAMENT
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import io.ktor.http.*
import io.ktor.server.application.*
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
            is Chain -> {
                field("Thickness", line.thickness)
                showColorSchemeItemPart(call, state, line.main, "Main")
            }

            is OrnamentLine -> {
                showOrnament(call, state, line.ornament)
                field("Size", line.size)
            }

            is Wire -> {
                field("Thickness", line.thickness)
                showColorSchemeItemPart(call, state, line.main, "Main")
            }
        }
    }
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
            is Chain -> {
                selectValue("Thickness", combine(param, SIZE), Size.entries, line.thickness)
                editColorSchemeItemPart(state, line.main, combine(param, MAIN), "Main")
            }

            is OrnamentLine -> {
                editOrnament(state, line.ornament, param = combine(param, ORNAMENT))
                selectValue("Size", combine(param, SIZE), Size.entries, line.size)
            }

            is Wire -> {
                selectValue("Thickness", combine(param, SIZE), Size.entries, line.thickness)
                editColorSchemeItemPart(state, line.main, combine(param, MAIN), "Main")
            }
        }
    }
}

// parse

fun parseLineStyle(parameters: Parameters, param: String): LineStyle {
    val type = parse(parameters, combine(param, STYLE), LineStyleType.Wire)

    return when (type) {
        LineStyleType.Chain -> Chain(
            parse(parameters, combine(param, SIZE), Size.Medium),
            parseColorSchemeItemPart(parameters, combine(param, MAIN)),
        )

        LineStyleType.Ornament -> OrnamentLine(
            parseOrnament(parameters, combine(param, ORNAMENT)),
            parse(parameters, combine(param, SIZE), Size.Medium),
        )

        LineStyleType.Wire -> Wire(
            parse(parameters, combine(param, SIZE), Size.Medium),
            parseColorSchemeItemPart(parameters, combine(param, MAIN)),
        )
    }
}

