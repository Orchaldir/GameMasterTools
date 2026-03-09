package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.STRAP
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BELT_STRAP_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.Belt
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBelt(
    call: ApplicationCall,
    state: State,
    belt: Belt,
) {
    showBuckle(call, state, belt.buckle)
    showItemPart(call, state, belt.strap, "Strap")
    showBeltHoles(belt.holes)
}

// edit

fun HtmlBlockTag.editBelt(
    state: State,
    belt: Belt,
) {
    editBuckle(state, belt.buckle)
    editItemPart(state, belt.strap, STRAP, "Strap", BELT_STRAP_MATERIALS)
    editBeltHoles(belt.holes)
}

// parse

fun parseBelt(parameters: Parameters) = Belt(
    parseBuckle(parameters),
    parseItemPart(parameters, STRAP, BELT_STRAP_MATERIALS),
    parseBeltHoles(parameters),
)
