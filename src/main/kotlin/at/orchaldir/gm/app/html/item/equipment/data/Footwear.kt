package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.FOOTWEAR
import at.orchaldir.gm.app.SHAFT
import at.orchaldir.gm.app.SOLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.rpg.combat.parseArmorStats
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.FOOTWEAR_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.Footwear
import at.orchaldir.gm.core.model.item.equipment.style.FootwearStyle
import at.orchaldir.gm.core.model.util.part.CLOTHING_MATERIALS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showFootwear(
    call: ApplicationCall,
    state: State,
    footwear: Footwear,
) {
    field("Style", footwear.style)
    showItemPart(call, state, footwear.shaft, "Shaft")
    if (footwear.style.hasSole()) {
        showItemPart(call, state, footwear.sole, "Sole")
    }
}

// edit

fun HtmlBlockTag.editFootwear(
    state: State,
    footwear: Footwear,
) {
    selectValue("Style", FOOTWEAR, FootwearStyle.entries, footwear.style)
    editItemPart(state, footwear.shaft, SHAFT, "Shaft", FOOTWEAR_MATERIALS)
    if (footwear.style.hasSole()) {
        editItemPart(state, footwear.sole, SOLE, "Sole", FOOTWEAR_MATERIALS)
    }
}

// parse

fun parseFootwear(parameters: Parameters) = Footwear(
    parse(parameters, FOOTWEAR, FootwearStyle.Shoes),
    parseItemPart(parameters, SHAFT, FOOTWEAR_MATERIALS),
    parseItemPart(parameters, SOLE, FOOTWEAR_MATERIALS),
    parseArmorStats(parameters),
)