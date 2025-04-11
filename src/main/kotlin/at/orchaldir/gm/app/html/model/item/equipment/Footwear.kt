package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.item.*
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Footwear
import at.orchaldir.gm.core.model.item.equipment.style.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showFootwear(
    call: ApplicationCall,
    state: State,
    footwear: Footwear,
) {
    field("Style", footwear.style)
    showFillItemPart(call, state, footwear.shaft, "Shaft")
    if (footwear.style.hasSole()) {
        showColorItemPart(call, state, footwear.sole, "Sole")
    }
}

// edit

fun FORM.editFootwear(
    state: State,
    footwear: Footwear,
) {
    selectValue("Style", FOOTWEAR, FootwearStyle.entries, footwear.style, true)
    editFillItemPart(state, footwear.shaft, SHAFT, "Shaft")
    if (footwear.style.hasSole()) {
        editColorItemPart(state, footwear.sole, SOLE, "Sole")
    }
}

// parse

fun parseFootwear(parameters: Parameters) = Footwear(
    parse(parameters, FOOTWEAR, FootwearStyle.Shoes),
    parseFillItemPart(parameters, SHAFT),
    parseColorItemPart(parameters, SOLE),
)