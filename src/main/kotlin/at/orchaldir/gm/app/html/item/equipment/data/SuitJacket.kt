package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.POCKET
import at.orchaldir.gm.app.SLEEVE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.SuitJacket
import at.orchaldir.gm.core.model.item.equipment.style.NECKLINES_WITH_SLEEVES
import at.orchaldir.gm.core.model.item.equipment.style.NecklineType
import at.orchaldir.gm.core.model.item.equipment.style.PocketStyle
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSuitJacket(
    call: ApplicationCall,
    state: State,
    data: SuitJacket,
) {
    showNeckline(call, state, data.neckline)
    field("Sleeve Style", data.sleeveStyle)
    showOpening(call, state, data.opening)
    field("Pocket Style", data.pocketStyle)
    showItemPart(call, state, data.main)
}

// edit

fun HtmlBlockTag.editSuitJacket(
    state: State,
    data: SuitJacket,
) {
    editNeckline(state, data.neckline, NECKLINES_WITH_SLEEVES)
    selectSleeveStyle(SleeveStyle.entries, data.sleeveStyle)
    editOpening(state, data.opening)
    selectPocketStyle(PocketStyle.entries, data.pocketStyle)
    editItemPart(state, data.main, MAIN, allowedTypes = CLOTHING_MATERIALS)
}


// parse

fun parseSuitJacket(
    state: State,
    parameters: Parameters,
) = SuitJacket(
    parseNeckline(state, parameters, NecklineType.V),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
    parseOpening(state, parameters),
    parse(parameters, combine(POCKET, STYLE), PocketStyle.None),
    parseItemPart(state, parameters, MAIN, CLOTHING_MATERIALS),
)

