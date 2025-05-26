package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.BORDER
import at.orchaldir.gm.app.ORNAMENT
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.math.parseComplexShape
import at.orchaldir.gm.app.html.math.selectComplexShape
import at.orchaldir.gm.app.html.math.showComplexShape
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.part.editColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showColorSchemeItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.Ornament
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentType
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentWithBorder
import at.orchaldir.gm.core.model.item.equipment.style.SimpleOrnament
import at.orchaldir.gm.utils.math.shape.SHAPES_WITHOUT_CROSS
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
                showComplexShape(ornament.shape)
                showFillLookupItemPart(call, state, ornament.part)
            }

            is OrnamentWithBorder -> {
                showComplexShape(ornament.shape)
                showFillLookupItemPart(call, state, ornament.center, "Center")
                showColorSchemeItemPart(call, state, ornament.border, "Border")
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
                selectComplexShape(ornament.shape, combine(param, SHAPE))
                editFillLookupItemPart(state, ornament.part, param)
            }

            is OrnamentWithBorder -> {
                selectComplexShape(
                    ornament.shape,
                    combine(param, SHAPE),
                    SHAPES_WITHOUT_CROSS,
                )
                editFillLookupItemPart(
                    state,
                    ornament.center,
                    param,
                    "Center",
                )
                editColorSchemeItemPart(
                    state,
                    ornament.border,
                    combine(param, BORDER),
                    "Border",
                )
            }
        }
    }
}

// parse

fun parseOrnament(parameters: Parameters, param: String = ORNAMENT): Ornament {
    val type = parse(parameters, combine(param, TYPE), OrnamentType.Simple)

    return when (type) {
        OrnamentType.Simple -> SimpleOrnament(
            parseComplexShape(parameters, combine(param, SHAPE)),
            parseFillLookupItemPart(parameters, param),
        )

        OrnamentType.Border -> OrnamentWithBorder(
            parseComplexShape(parameters, combine(param, SHAPE)),
            parseFillLookupItemPart(parameters, param),
            parseColorSchemeItemPart(parameters, combine(param, BORDER)),
        )
    }
}