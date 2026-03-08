package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.equipment.style.editBeltHoles
import at.orchaldir.gm.app.html.item.equipment.style.editBuckle
import at.orchaldir.gm.app.html.item.equipment.style.parseBeltHoles
import at.orchaldir.gm.app.html.item.equipment.style.parseBuckle
import at.orchaldir.gm.app.html.item.equipment.style.showBeltHoles
import at.orchaldir.gm.app.html.item.equipment.style.showBuckle
import at.orchaldir.gm.app.html.util.part.editFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showFillLookupItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Belt
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
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
    editItemPart(state, belt.strap, STRAP, "Strap", CLOTHING_MATERIALS)
    editBeltHoles(belt.holes)
}

// parse

fun parseBelt(parameters: Parameters) = Belt(
    parseBuckle(parameters),
    parseItemPart(parameters, STRAP),
    parseBeltHoles(parameters),
)
