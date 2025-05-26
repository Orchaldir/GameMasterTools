package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.HAT
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Hat
import at.orchaldir.gm.core.model.item.equipment.style.HatStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showHat(
    call: ApplicationCall,
    state: State,
    hat: Hat,
) {
    field("Style", hat.style)
    showFillLookupItemPart(call, state, hat.main, "Main")
}

// edit

fun FORM.editHat(
    state: State,
    hat: Hat,
) {
    selectValue("Style", HAT, HatStyle.entries, hat.style)
    editFillLookupItemPart(state, hat.main, MAIN, "Main")
}

// parse

fun parseHat(parameters: Parameters): Hat = Hat(
    parse(parameters, HAT, HatStyle.TopHat),
    parseFillLookupItemPart(parameters, MAIN),
)
