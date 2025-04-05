package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseMaterialId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Color
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showOrnament(
    call: ApplicationCall,
    state: State,
    ornament: Ornament,
    label: String = "Ornament",
) {
    showDetails(label, true) {
        field("Type", ornament.getType())

        when (ornament) {
            is SimpleOrnament -> {
                field("Shape", ornament.shape)
                field("Color", ornament.color)
                fieldLink("Material", call, state, ornament.material)
            }

            is OrnamentWithBorder -> {
                field("Shape", ornament.shape)
                field("Color", ornament.color)
                fieldLink("Material", call, state, ornament.material)
                field("Border Color", ornament.borderColor)
                fieldLink("Border Material", call, state, ornament.borderMaterial)
            }
        }
    }
}

// edit

fun FORM.editOrnament(
    state: State,
    ornament: Ornament,
    param: String = ORNAMENT,
    label: String = "Ornament",
) {
    showDetails(label, true) {
        selectValue("Type", combine(param, TYPE), OrnamentType.entries, ornament.getType(), true)

        when (ornament) {
            is SimpleOrnament -> {
                selectValue("Shape", combine(param, SHAPE), OrnamentShape.entries, ornament.shape, true)
                selectColor(ornament.color, combine(param, COLOR))
                selectMaterial(state, ornament.material, combine(param, MATERIAL))
            }

            is OrnamentWithBorder -> {
                selectValue("Shape", combine(param, SHAPE), OrnamentShape.entries, ornament.shape, true)
                selectColor(ornament.color, combine(param, COLOR))
                selectMaterial(state, ornament.material, combine(param, MATERIAL))
                selectColor(ornament.color, combine(param, BORDER, COLOR), "Border Color")
                selectMaterial(state, ornament.material, combine(param, BORDER, MATERIAL), "Border Material")
            }
        }
    }
}

// parse

fun parseOrnament(parameters: Parameters, param: String = ORNAMENT): Ornament {
    val type = parse(parameters, combine(param, TYPE), OrnamentType.Simple)

    return when (type) {
        OrnamentType.Simple -> SimpleOrnament(
            parse(parameters, combine(param, SHAPE), OrnamentShape.Circle),
            parse(parameters, combine(param, COLOR), Color.Gold),
            parseMaterialId(parameters, combine(param, MATERIAL)),
        )

        OrnamentType.Border -> OrnamentWithBorder(
            parse(parameters, combine(param, SHAPE), OrnamentShape.Circle),
            parse(parameters, combine(param, COLOR), Color.Red),
            parseMaterialId(parameters, combine(param, MATERIAL)),
            parse(parameters, combine(param, BORDER, COLOR), Color.Gold),
            parseMaterialId(parameters, combine(param, BORDER, MATERIAL)),
        )
    }
}