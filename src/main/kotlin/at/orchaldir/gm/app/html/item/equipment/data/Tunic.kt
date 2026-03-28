package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.editNeckline
import at.orchaldir.gm.app.html.item.equipment.style.parseNeckline
import at.orchaldir.gm.app.html.item.equipment.style.selectSleeveStyle
import at.orchaldir.gm.app.html.item.equipment.style.showNeckline
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Tunic
import at.orchaldir.gm.core.model.item.equipment.style.NECKLINES_WITH_SLEEVES
import at.orchaldir.gm.core.model.item.equipment.style.NecklineType
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
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
    showNeckline(call, state, data.neckline)
    field("Sleeve Style", data.sleeveStyle)
    showItemPart(call, state, data.main)
}

// edit

fun HtmlBlockTag.editTunic(
    state: State,
    data: Tunic,
) {
    selectValue("Length", LENGTH, OuterwearLength.entries, data.length)
    editNeckline(state, data.neckline, NECKLINES_WITH_SLEEVES)
    selectSleeveStyle(SleeveStyle.entries, data.sleeveStyle)
    editItemPart(state, data.main, MAIN, allowedTypes = CLOTHING_MATERIALS)
}

// parse

fun parseTunic(
    state: State,
    parameters: Parameters,
) = Tunic(
    parseItemPart(state, parameters, MAIN, CLOTHING_MATERIALS),
    parse(parameters, LENGTH, OuterwearLength.Hip),
    parseNeckline(state, parameters, NecklineType.V),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
)
