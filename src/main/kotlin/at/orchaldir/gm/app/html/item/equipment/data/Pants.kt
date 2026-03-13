package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.PANTS
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.PANTS_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.Pants
import at.orchaldir.gm.core.model.item.equipment.style.PantsStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPants(
    call: ApplicationCall,
    state: State,
    pants: Pants,
) {
    field("Style", pants.style)
    showItemPart(call, state, pants.main)
}

// edit

fun HtmlBlockTag.editPants(
    state: State,
    pants: Pants,
) {
    selectValue("Style", PANTS, PantsStyle.entries, pants.style)
    editItemPart(state, pants.main, MAIN, allowedTypes = PANTS_MATERIALS)
}

// parse

fun parsePants(
    state: State,
    parameters: Parameters,
) = Pants(
    parse(parameters, PANTS, PantsStyle.Regular),
    parseItemPart(state, parameters, MAIN, PANTS_MATERIALS),
)