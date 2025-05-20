package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.BORDER
import at.orchaldir.gm.app.ORNAMENT
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.*
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.*
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
                showFillItemPart(call, state, ornament.part)
            }

            is OrnamentWithBorder -> {
                field("Shape", ornament.shape)
                showFillItemPart(call, state, ornament.center, "Center")
                showColorItemPart(call, state, ornament.border, "Border")
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
        selectValue("Type", combine(param, TYPE), OrnamentType.entries, ornament.getType())

        when (ornament) {
            is SimpleOrnament -> {
                selectValue("Shape", combine(param, SHAPE), OrnamentShape.entries, ornament.shape)
                editFillItemPart(state, ornament.part, param)
            }

            is OrnamentWithBorder -> {
                selectValue("Shape", combine(param, SHAPE), OrnamentShape.entries, ornament.shape)
                editFillItemPart(state, ornament.center, param)
                editColorItemPart(state, ornament.border, combine(param, BORDER))
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
            parseFillItemPart(parameters, param),
        )

        OrnamentType.Border -> OrnamentWithBorder(
            parse(parameters, combine(param, SHAPE), OrnamentShape.Circle),
            parseFillItemPart(parameters, param),
            parseColorItemPart(parameters, combine(param, BORDER)),
        )
    }
}