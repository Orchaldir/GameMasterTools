package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.SHIRT_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.Shirt
import at.orchaldir.gm.core.model.item.equipment.style.NECKLINES_WITH_SLEEVES
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
    showNeckline(call, state, shirt.neckline)
    field("Sleeve Style", shirt.sleeveStyle)
    showItemPart(call, state, shirt.main)
}

// edit

fun HtmlBlockTag.editShirt(
    state: State,
    shirt: Shirt,
) {
    editNeckline(state, shirt.neckline, NECKLINES_WITH_SLEEVES)
    selectSleeveStyle(
        SleeveStyle.entries,
        shirt.sleeveStyle,
    )
    editItemPart(state, shirt.main, MAIN, allowedTypes = SHIRT_MATERIALS)
}

// parse

fun parseShirt(
    state: State,
    parameters: Parameters,
): Shirt {
    val neckline = parseNeckline(state, parameters)

    return Shirt(
        neckline,
        parseSleeveStyle(parameters, neckline),
        parseItemPart(state, parameters, MAIN, SHIRT_MATERIALS),
    )
}