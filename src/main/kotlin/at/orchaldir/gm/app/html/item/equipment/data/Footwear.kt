package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.html.item.equipment.style.editFootwearStyle
import at.orchaldir.gm.app.html.item.equipment.style.parseFootwearStyle
import at.orchaldir.gm.app.html.item.equipment.style.showFootwearStyle
import at.orchaldir.gm.app.html.rpg.combat.parseArmorStats
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Footwear
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showFootwear(
    call: ApplicationCall,
    state: State,
    footwear: Footwear,
) {
    showFootwearStyle(call, state, footwear.style)
}

// edit

fun HtmlBlockTag.editFootwear(
    state: State,
    footwear: Footwear,
) {
    editFootwearStyle(state, footwear.style)
}

// parse

fun parseFootwear(
    state: State,
    parameters: Parameters,
) = Footwear(
    parseFootwearStyle(state, parameters),
    parseArmorStats(parameters),
)