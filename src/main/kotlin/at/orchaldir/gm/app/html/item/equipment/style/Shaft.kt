package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.SHAFT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.SHAFT_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.style.Shaft
import at.orchaldir.gm.core.model.item.equipment.style.ShaftType
import at.orchaldir.gm.core.model.item.equipment.style.SimpleShaft
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showShaft(
    call: ApplicationCall,
    state: State,
    shaft: Shaft,
) {
    showDetails("Shaft") {
        field("Style", shaft.getType())

        when (shaft) {
            is SimpleShaft -> showItemPart(call, state, shaft.part)
        }
    }
}

// edit

fun HtmlBlockTag.editShaft(
    state: State,
    shaft: Shaft,
    param: String = SHAFT,
) {
    showDetails("Shaft", true) {
        selectValue("Type", param, ShaftType.entries, shaft.getType())

        when (shaft) {
            is SimpleShaft -> editItemPart(
                state,
                shaft.part,
                combine(param, MAIN),
                allowedTypes = SHAFT_MATERIALS,
            )
        }
    }
}

// parse

fun parseShaft(
    state: State,
    parameters: Parameters,
    param: String = SHAFT,
) = when (parse(parameters, param, ShaftType.Simple)) {
    ShaftType.Simple -> SimpleShaft(
        parseItemPart(state, parameters, combine(param, MAIN), SHAFT_MATERIALS),
    )
}