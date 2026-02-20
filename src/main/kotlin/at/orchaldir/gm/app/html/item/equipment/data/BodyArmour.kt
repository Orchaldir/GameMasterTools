package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.SLEEVE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.editArmourStyle
import at.orchaldir.gm.app.html.item.equipment.style.parseArmourStyle
import at.orchaldir.gm.app.html.item.equipment.style.selectSleeveStyle
import at.orchaldir.gm.app.html.item.equipment.style.showArmourStyle
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
    //field("Length", armour.length) TODO
    field("Sleeve Style", armour.sleeveStyle)
    showArmourStyle(call, state, armour.style)
}

// edit

fun HtmlBlockTag.editBodyArmour(
    state: State,
    armour: BodyArmour,
) {
    //selectValue("Length", LENGTH, OuterwearLength.entries, armour.length)
    selectSleeveStyle(SleeveStyle.entries, armour.sleeveStyle)
    editArmourStyle(state, armour.style)
}

// parse

fun parseBodyArmour(parameters: Parameters) = BodyArmour(
    parseArmourStyle(parameters),
    SameLegArmour(),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
    parseArmorStats(parameters),
)
