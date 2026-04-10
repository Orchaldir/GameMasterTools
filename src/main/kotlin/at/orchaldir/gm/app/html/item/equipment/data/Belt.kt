package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.html.item.equipment.style.editBeltStyle
import at.orchaldir.gm.app.html.item.equipment.style.parseBeltStyle
import at.orchaldir.gm.app.html.item.equipment.style.showBeltStyle
import at.orchaldir.gm.core.model.State
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
