package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.SuitJacket
import at.orchaldir.gm.core.model.item.equipment.style.NECKLINES_WITH_SLEEVES
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle
import at.orchaldir.gm.core.model.item.equipment.style.PocketStyle
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSuitJacket(
    call: ApplicationCall,
    state: State,
    data: SuitJacket,
) {
    field("Neckline Style", data.necklineStyle)
    field("Sleeve Style", data.sleeveStyle)
    showOpeningStyle(call, state, data.openingStyle)
    field("Pocket Style", data.pocketStyle)
    showFillLookupItemPart(call, state, data.main, "Main")
}

// edit

fun HtmlBlockTag.editSuitJacket(
    state: State,
    data: SuitJacket,
) {
    selectNecklineStyle(NECKLINES_WITH_SLEEVES, data.necklineStyle)
    selectSleeveStyle(SleeveStyle.entries, data.sleeveStyle)
    selectOpeningStyle(state, data.openingStyle)
    selectPocketStyle(PocketStyle.entries, data.pocketStyle)
    editFillLookupItemPart(state, data.main, MAIN)
}


// parse

fun parseSuitJacket(parameters: Parameters) = SuitJacket(
    parse(parameters, combine(NECKLINE, STYLE), NecklineStyle.DeepV),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
    parseOpeningStyle(parameters),
    parse(parameters, combine(POCKET, STYLE), PocketStyle.None),
    parseFillLookupItemPart(parameters, MAIN),
)

