package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.PANTS
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.editFillLookupItemPart
import at.orchaldir.gm.app.html.item.parseFillLookupItemPart
import at.orchaldir.gm.app.html.item.showFillLookupItemPart
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Pants
import at.orchaldir.gm.core.model.item.equipment.style.PantsStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPants(
    call: ApplicationCall,
    state: State,
    pants: Pants,
) {
    field("Style", pants.style)
    showFillLookupItemPart(call, state, pants.main, "Main")
}

// edit

fun FORM.editPants(
    state: State,
    pants: Pants,
) {
    selectValue("Style", PANTS, PantsStyle.entries, pants.style)
    editFillLookupItemPart(state, pants.main, MAIN, "Main")
}

// parse

fun parsePants(parameters: Parameters): Pants = Pants(
    parse(parameters, PANTS, PantsStyle.Regular),
    parseFillLookupItemPart(parameters, MAIN),
)