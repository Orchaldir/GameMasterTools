package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.NECKLINE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.parseSleeveStyle
import at.orchaldir.gm.app.html.item.equipment.style.selectNecklineStyle
import at.orchaldir.gm.app.html.item.equipment.style.selectSleeveStyle
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.item.equipment.style.NECKLINES_WITH_SLEEVES
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showShirt(
    call: ApplicationCall,
    state: State,
    shirt: Shirt,
) {
    field("Neckline Style", shirt.necklineStyle)
    field("Sleeve Style", shirt.sleeveStyle)
    showFillLookupItemPart(call, state, shirt.main, "Main")
}

// edit

fun HtmlBlockTag.editShirt(
    state: State,
    shirt: Shirt,
) {
    selectNecklineStyle(NECKLINES_WITH_SLEEVES, shirt.necklineStyle)
    selectSleeveStyle(
        SleeveStyle.entries,
        shirt.sleeveStyle,
    )
    editFillLookupItemPart(state, shirt.main, MAIN, "Main")
}

// parse

fun parseShirt(parameters: Parameters): Shirt {
    val neckline = parse(parameters, combine(NECKLINE, STYLE), NecklineStyle.None)

    return Shirt(
        neckline,
        parseSleeveStyle(parameters, neckline),
        parseFillLookupItemPart(parameters, MAIN),
    )
}