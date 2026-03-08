package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.HAT
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Hat
import at.orchaldir.gm.core.model.item.equipment.style.HatStyle
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showHat(
    call: ApplicationCall,
    state: State,
    hat: Hat,
) {
    field("Style", hat.style)
    showItemPart(call, state, hat.main)
}

// edit

fun HtmlBlockTag.editHat(
    state: State,
    hat: Hat,
) {
    selectValue("Style", HAT, HatStyle.entries, hat.style)
    editItemPart(state, hat.main, MAIN, allowedTypes = CLOTHING_MATERIALS)
}

// parse

fun parseHat(parameters: Parameters): Hat = Hat(
    parse(parameters, HAT, HatStyle.TopHat),
    parseItemPart(parameters, MAIN),
)
