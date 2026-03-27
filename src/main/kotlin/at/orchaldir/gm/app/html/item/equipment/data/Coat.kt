package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Coat
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCoat(
    call: ApplicationCall,
    state: State,
    data: Coat,
) {
    field("Length", data.length)
    showNeckline(call, state, data.neckline)
    field("Sleeve Style", data.sleeveStyle)
    showOpeningStyle(call, state, data.openingStyle)
    field("Pocket Style", data.pocketStyle)
    showItemPart(call, state, data.main)
}

// edit

fun HtmlBlockTag.editCoat(
    state: State,
    data: Coat,
) {
    selectValue("Length", LENGTH, OuterwearLength.entries, data.length)
    editNeckline(state, data.neckline, NECKLINES_WITH_SLEEVES)
    selectSleeveStyle(SleeveStyle.entries, data.sleeveStyle)
    selectOpeningStyle(state, data.openingStyle)
    selectPocketStyle(PocketStyle.entries, data.pocketStyle)
    editItemPart(state, data.main, MAIN, allowedTypes = CLOTHING_MATERIALS)
}

// parse

fun parseCoat(
    state: State,
    parameters: Parameters,
) = Coat(
    parseItemPart(state, parameters, MAIN, CLOTHING_MATERIALS),
    parse(parameters, LENGTH, OuterwearLength.Hip),
    parseNeckline(parameters, NecklineType.V),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
    parseOpeningStyle(state, parameters),
    parse(parameters, combine(POCKET, STYLE), PocketStyle.None),
)
