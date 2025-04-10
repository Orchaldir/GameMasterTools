package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseMaterialId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Color
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showOrnament(
    call: ApplicationCall,
    state: State,
    ornament: Ornament,
    label: String = "Ornament",
) {
    showDetails(label) {
        field("Type", ornament.getType())

        when (ornament) {
            is SimpleOrnament -> {
                field("Shape", ornament.shape)
                showLook(call, state, ornament.color, ornament.material)
            }

            is OrnamentWithBorder -> {
                field("Shape", ornament.shape)
                showLook(call, state, ornament.color, ornament.material, "Center")
                showLook(call, state, ornament.borderColor, ornament.borderMaterial, "Border")
            }
        }
    }
}

// edit

fun HtmlBlockTag.editOrnament(
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
                editLook(state, ornament.color, ornament.material, param)
            }

            is OrnamentWithBorder -> {
                selectValue("Shape", combine(param, SHAPE), OrnamentShape.entries, ornament.shape, true)
                editLook(state, ornament.color, ornament.material, param)
                editLook(state, ornament.borderColor, ornament.borderMaterial, combine(param, BORDER), "Border")
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
            parse(parameters, combine(param, BORDER, COLOR), Color.Gold),
            parseMaterialId(parameters, combine(param, MATERIAL)),
            parseMaterialId(parameters, combine(param, BORDER, MATERIAL)),
        )
    }
}