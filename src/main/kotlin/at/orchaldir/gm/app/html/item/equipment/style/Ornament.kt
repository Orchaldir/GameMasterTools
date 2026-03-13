package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.math.parseComplexShape
import at.orchaldir.gm.app.html.math.selectComplexShape
import at.orchaldir.gm.app.html.math.showComplexShape
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.ORNAMENT_MATERIALS
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
                showItemPart(call, state, ornament.part)
            }

            is OrnamentWithBorder -> {
                showComplexShape(ornament.shape)
                showItemPart(call, state, ornament.center, "Center")
                showItemPart(call, state, ornament.border, "Border")
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
                editItemPart(
                    state,
                    ornament.part,
                    combine(param, MAIN),
                    allowedTypes = ORNAMENT_MATERIALS,
                )
            }

            is OrnamentWithBorder -> {
                selectComplexShape(
                    ornament.shape,
                    combine(param, SHAPE),
                    SHAPES_WITHOUT_CROSS,
                )
                editItemPart(
                    state,
                    ornament.center,
                    combine(param, MIDDLE),
                    "Center",
                    ORNAMENT_MATERIALS,
                )
                editItemPart(
                    state,
                    ornament.border,
                    combine(param, MAIN),
                    "Border",
                    ORNAMENT_MATERIALS,
                )
            }
        }
    }
}

// parse

fun parseOrnament(
    state: State,
    parameters: Parameters,
    param: String = ORNAMENT,
): Ornament {
    val type = parse(parameters, combine(param, TYPE), OrnamentType.Simple)

    return when (type) {
        OrnamentType.Simple -> SimpleOrnament(
            parseComplexShape(parameters, combine(param, SHAPE)),
            parseItemPart(state, parameters, combine(param, MAIN), ORNAMENT_MATERIALS),
        )

        OrnamentType.Border -> OrnamentWithBorder(
            parseComplexShape(parameters, combine(param, SHAPE)),
            parseItemPart(state, parameters, combine(param, MIDDLE), ORNAMENT_MATERIALS),
            parseItemPart(state, parameters, combine(param, MAIN), ORNAMENT_MATERIALS),
        )
    }
}