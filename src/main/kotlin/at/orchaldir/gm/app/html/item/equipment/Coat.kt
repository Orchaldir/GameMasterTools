package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Coat
import at.orchaldir.gm.core.model.item.equipment.style.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCoat(
    call: ApplicationCall,
    state: State,
    data: Coat,
) {
    field("Length", data.length)
    field("Neckline Style", data.necklineStyle)
    field("Sleeve Style", data.sleeveStyle)
    showOpeningStyle(call, state, data.openingStyle)
    field("Pocket Style", data.pocketStyle)
    showFillLookupItemPart(call, state, data.main, "Main")
}

// edit

fun FORM.editCoat(
    state: State,
    data: Coat,
) {
    selectValue("Length", LENGTH, OuterwearLength.entries, data.length)
    selectNecklineStyle(NECKLINES_WITH_SLEEVES, data.necklineStyle)
    selectSleeveStyle(SleeveStyle.entries, data.sleeveStyle)
    selectOpeningStyle(state, data.openingStyle)
    selectPocketStyle(PocketStyle.entries, data.pocketStyle)
    editFillLookupItemPart(state, data.main, MAIN)
}

// parse

fun parseCoat(parameters: Parameters) = Coat(
    parse(parameters, LENGTH, OuterwearLength.Hip),
    parse(parameters, combine(NECKLINE, STYLE), NecklineStyle.DeepV),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
    parseOpeningStyle(parameters),
    parse(parameters, combine(POCKET, STYLE), PocketStyle.None),
    parseFillLookupItemPart(parameters, MAIN),
)
