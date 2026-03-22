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
import at.orchaldir.gm.core.model.item.equipment.Tunic
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTunic(
    call: ApplicationCall,
    state: State,
    data: Tunic,
) {
    field("Length", data.length)
    field("Neckline Style", data.necklineStyle)
    field("Sleeve Style", data.sleeveStyle)
    field("Pocket Style", data.pocketStyle)
    showItemPart(call, state, data.main)
}

// edit

fun HtmlBlockTag.editTunic(
    state: State,
    data: Tunic,
) {
    selectValue("Length", LENGTH, OuterwearLength.entries, data.length)
    selectNecklineStyle(NECKLINES_WITH_SLEEVES, data.necklineStyle)
    selectSleeveStyle(SleeveStyle.entries, data.sleeveStyle)
    selectPocketStyle(PocketStyle.entries, data.pocketStyle)
    editItemPart(state, data.main, MAIN, allowedTypes = CLOTHING_MATERIALS)
}

// parse

fun parseTunic(
    state: State,
    parameters: Parameters,
) = Tunic(
    parseItemPart(state, parameters, MAIN, CLOTHING_MATERIALS),
    parse(parameters, LENGTH, OuterwearLength.Hip),
    parse(parameters, combine(NECKLINE, STYLE), NecklineStyle.DeepV),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
    parse(parameters, combine(POCKET, STYLE), PocketStyle.None),
)
