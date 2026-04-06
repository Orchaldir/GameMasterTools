package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.STRAP
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BELT_STRAP_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.Belt
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBelt(
    call: ApplicationCall,
    state: State,
    belt: Belt,
) {
    showBeltStyle(call, state, belt.style)
}

// edit

fun HtmlBlockTag.editBelt(
    state: State,
    belt: Belt,
) {
    editBeltStyle(state, belt.style)
}

// parse

fun parseBelt(
    state: State,
    parameters: Parameters,
) = Belt(
    parseBeltStyle(state, parameters),
)
