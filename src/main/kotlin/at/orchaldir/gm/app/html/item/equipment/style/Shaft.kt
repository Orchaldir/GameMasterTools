package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.SHAFT
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.Shaft
import at.orchaldir.gm.core.model.item.equipment.style.ShaftType
import at.orchaldir.gm.core.model.item.equipment.style.SimpleShaft
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
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
            is SimpleShaft -> showFillLookupItemPart(call, state, shaft.part)
        }
    }
}

// edit

fun FORM.editShaft(
    state: State,
    shaft: Shaft,
    param: String = SHAFT,
) {
    showDetails("Shaft", true) {
        selectValue("Type", param, ShaftType.entries, shaft.getType())

        when (shaft) {
            is SimpleShaft -> editFillLookupItemPart(state, shaft.part, combine(param, MAIN))
        }
    }
}

// parse

fun parseShaft(
    parameters: Parameters,
    param: String = SHAFT,
) = when (parse(parameters, param, ShaftType.Simple)) {
    ShaftType.Simple -> SimpleShaft(
        parseFillLookupItemPart(parameters, combine(param, MAIN)),
    )
}