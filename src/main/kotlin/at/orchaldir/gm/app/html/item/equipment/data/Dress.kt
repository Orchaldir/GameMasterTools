package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.SKIRT_STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.DRESS_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.Dress
import at.orchaldir.gm.core.model.item.equipment.style.SkirtStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showDress(
    call: ApplicationCall,
    state: State,
    dress: Dress,
) {
    showNeckline(call, state, dress.neckline)
    field("Skirt Style", dress.skirtStyle)
    field("Sleeve Style", dress.sleeveStyle)
    showItemPart(call, state, dress.main)
}

// edit

fun HtmlBlockTag.editDress(
    state: State,
    dress: Dress,
) {
    editNeckline(state, dress.neckline)
    selectValue("Skirt Style", SKIRT_STYLE, SkirtStyle.entries, dress.skirtStyle)
    selectSleeveStyle(
        dress.neckline.getSupportedSleevesStyles(),
        dress.sleeveStyle,
    )
    editItemPart(state, dress.main, MAIN, allowedTypes = DRESS_MATERIALS)
}

// parse

fun parseDress(
    state: State,
    parameters: Parameters,
): Dress {
    val neckline = parseNeckline(state, parameters)

    return Dress(
        neckline,
        parse(parameters, SKIRT_STYLE, SkirtStyle.Sheath),
        parseSleeveStyle(parameters, neckline),
        parseItemPart(state, parameters, MAIN, DRESS_MATERIALS),
    )
}
