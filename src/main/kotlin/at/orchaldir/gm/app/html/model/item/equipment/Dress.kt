package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.NECKLINE
import at.orchaldir.gm.app.SKIRT_STYLE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.item.editFillItemPart
import at.orchaldir.gm.app.html.model.item.parseFillItemPart
import at.orchaldir.gm.app.html.model.item.showFillItemPart
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Dress
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle
import at.orchaldir.gm.core.model.item.equipment.style.SkirtStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showDress(
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
    val neckline = parse(parameters, combine(NECKLINE, STYLE), NecklineStyle.None)

    return Dress(
        neckline,
        parse(parameters, SKIRT_STYLE, SkirtStyle.Sheath),
        parseSleeveStyle(parameters, neckline),
        parseFillItemPart(parameters, MAIN),
    )
}
