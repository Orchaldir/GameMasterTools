package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.item.editFillItemPart
import at.orchaldir.gm.app.html.model.item.parseFillItemPart
import at.orchaldir.gm.app.html.model.item.showFillItemPart
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.SuitJacket
import at.orchaldir.gm.core.model.item.equipment.style.NECKLINES_WITH_SLEEVES
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle
import at.orchaldir.gm.core.model.item.equipment.style.PocketStyle
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showSuitJacket(
    call: ApplicationCall,
    state: State,
    data: SuitJacket,
) {
    field("Neckline Style", data.necklineStyle)
    field("Sleeve Style", data.sleeveStyle)
    showOpeningStyle(call, state, data.openingStyle)
    field("Pocket Style", data.pocketStyle)
    showFillItemPart(call, state, data.main, "Main")
}

// edit

fun FORM.editSuitJacket(
    state: State,
    data: SuitJacket,
) {
    selectNecklineStyle(NECKLINES_WITH_SLEEVES, data.necklineStyle)
    selectSleeveStyle(SleeveStyle.entries, data.sleeveStyle)
    selectOpeningStyle(state, data.openingStyle)
    selectPocketStyle(PocketStyle.entries, data.pocketStyle)
    editFillItemPart(state, data.main, MAIN)
}


// parse

fun parseSuitJacket(parameters: Parameters) = SuitJacket(
    parse(parameters, combine(NECKLINE, STYLE), NecklineStyle.DeepV),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
    parseOpeningStyle(parameters),
    parse(parameters, combine(POCKET, STYLE), PocketStyle.None),
    parseFillItemPart(parameters, MAIN),
)

