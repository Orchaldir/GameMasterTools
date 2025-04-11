package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.html.model.parseMaterialId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showJewelryLine(
    call: ApplicationCall,
    state: State,
    line: JewelryLine,
    label: String,
) {
    showDetails(label) {
        field("Style", line.getType())

        when (line) {
            is Chain -> {
                field("Thickness", line.thickness)
                showLook(call, state, line.color, line.material)
            }

            is OrnamentLine -> {
                showOrnament(call, state, line.ornament)
                field("Size", line.size)
            }

            is Wire -> {
                field("Thickness", line.thickness)
                showLook(call, state, line.color, line.material)
            }
        }
    }
}

// edit

fun FORM.editJewelryLine(
    state: State,
    line: JewelryLine,
    label: String,
    param: String,
) {
    showDetails(label, true) {
        selectValue("Style", combine(param, STYLE), JewelryLineType.entries, line.getType(), true)

        when (line) {
            is Chain -> {
                selectValue("Thickness", combine(param, SIZE), Size.entries, line.thickness, true)
                editLook(state, line.color, line.material, param, "Wire")
            }

            is OrnamentLine -> {
                editOrnament(state, line.ornament, param = combine(param, ORNAMENT))
                selectValue("Size", combine(param, SIZE), Size.entries, line.size, true)
            }

            is Wire -> {
                selectValue("Thickness", combine(param, SIZE), Size.entries, line.thickness, true)
                editLook(state, line.color, line.material, param, "Wire")
            }
        }
    }
}

// parse

fun parseJewelryLine(parameters: Parameters, param: String): JewelryLine {
    val type = parse(parameters, combine(param, STYLE), JewelryLineType.Wire)

    return when (type) {
        JewelryLineType.Chain -> Chain(
            parse(parameters, combine(param, SIZE), Size.Medium),
            parse(parameters, combine(param, COLOR), Color.Gold),
            parseMaterialId(parameters, combine(param, MATERIAL)),
        )

        JewelryLineType.Ornament -> OrnamentLine(
            parseOrnament(parameters, combine(param, ORNAMENT)),
            parse(parameters, combine(param, SIZE), Size.Medium),
        )

        JewelryLineType.Wire -> Wire(
            parse(parameters, combine(param, SIZE), Size.Medium),
            parse(parameters, combine(param, COLOR), Color.Gold),
            parseMaterialId(parameters, combine(param, MATERIAL)),
        )
    }
}

