package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.item.*
import at.orchaldir.gm.app.html.model.parseMaterialId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Dress
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun BODY.showDress(
    call: ApplicationCall,
    state: State,
    dress: Dress,
) {
    field("Neckline Style", dress.necklineStyle)
    field("Skirt Style", dress.skirtStyle)
    field("Sleeve Style", dress.sleeveStyle)
    showFillItemPart(call, state, dress.main, "Main")
}

// edit

fun FORM.editDress(
    state: State,
    dress: Dress,
) {
    selectNecklineStyle(NecklineStyle.entries, dress.necklineStyle)
    selectValue("Skirt Style", SKIRT_STYLE, SkirtStyle.entries, dress.skirtStyle, true)
    selectSleeveStyle(
        dress.necklineStyle.getSupportsSleevesStyles(),
        dress.sleeveStyle,
    )
    editFillItemPart(state, dress.main, MAIN)
}

// parse

fun parseDress(parameters: Parameters): Dress {
    val neckline = parse(parameters, NECKLINE_STYLE, NecklineStyle.None)

    return Dress(
        neckline,
        parse(parameters, SKIRT_STYLE, SkirtStyle.Sheath),
        parseSleeveStyle(parameters, neckline),
        parseFillItemPart(parameters, MAIN),
    )
}
