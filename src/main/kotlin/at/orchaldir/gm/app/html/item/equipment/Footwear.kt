package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.FOOTWEAR
import at.orchaldir.gm.app.SHAFT
import at.orchaldir.gm.app.SOLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.*
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Footwear
import at.orchaldir.gm.core.model.item.equipment.style.FootwearStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showFootwear(
    call: ApplicationCall,
    state: State,
    footwear: Footwear,
) {
    field("Style", footwear.style)
    showFillLookupItemPart(call, state, footwear.shaft, "Shaft")
    if (footwear.style.hasSole()) {
        showColorItemPart(call, state, footwear.sole, "Sole")
    }
}

// edit

fun FORM.editFootwear(
    state: State,
    footwear: Footwear,
) {
    selectValue("Style", FOOTWEAR, FootwearStyle.entries, footwear.style)
    editFillLookupItemPart(state, footwear.shaft, SHAFT, "Shaft")
    if (footwear.style.hasSole()) {
        editColorItemPart(state, footwear.sole, SOLE, "Sole")
    }
}

// parse

fun parseFootwear(parameters: Parameters) = Footwear(
    parse(parameters, FOOTWEAR, FootwearStyle.Shoes),
    parseFillLookupItemPart(parameters, SHAFT),
    parseColorItemPart(parameters, SOLE),
)