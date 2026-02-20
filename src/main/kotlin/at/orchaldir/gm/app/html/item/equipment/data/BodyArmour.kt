package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.SLEEVE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.editArmourStyle
import at.orchaldir.gm.app.html.item.equipment.style.editLegArmourStyle
import at.orchaldir.gm.app.html.item.equipment.style.parseArmourStyle
import at.orchaldir.gm.app.html.item.equipment.style.parseLegArmourStyle
import at.orchaldir.gm.app.html.item.equipment.style.selectSleeveStyle
import at.orchaldir.gm.app.html.item.equipment.style.showArmourStyle
import at.orchaldir.gm.app.html.item.equipment.style.showLegArmourStyle
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.rpg.combat.parseArmorStats
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.SameLegArmour
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBodyArmour(
    call: ApplicationCall,
    state: State,
    armour: BodyArmour,
) {
    showArmourStyle(call, state, armour.style)
    field("Sleeve Style", armour.sleeveStyle)
    showLegArmourStyle(call, state, armour.legStyle)
}

// edit

fun HtmlBlockTag.editBodyArmour(
    state: State,
    armour: BodyArmour,
) {
    editArmourStyle(state, armour.style)
    selectSleeveStyle(SleeveStyle.entries, armour.sleeveStyle)
    editLegArmourStyle(state, armour.legStyle)
}

// parse

fun parseBodyArmour(parameters: Parameters) = BodyArmour(
    parseArmourStyle(parameters),
    parseLegArmourStyle(parameters),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
    parseArmorStats(parameters),
)
