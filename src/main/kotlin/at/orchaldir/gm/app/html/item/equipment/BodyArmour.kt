package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.SLEEVE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.editArmourStyle
import at.orchaldir.gm.app.html.item.equipment.style.parseArmourStyle
import at.orchaldir.gm.app.html.item.equipment.style.selectSleeveStyle
import at.orchaldir.gm.app.html.item.equipment.style.showArmourStyle
import at.orchaldir.gm.app.html.rpg.combat.parseArmorStats
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
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
    field("Length", armour.length)
    field("Sleeve Style", armour.sleeveStyle)
    showArmourStyle(call, state, armour.style)
}

// edit

fun HtmlBlockTag.editBodyArmour(
    state: State,
    armour: BodyArmour,
) {
    selectValue("Length", LENGTH, OuterwearLength.entries, armour.length)
    selectSleeveStyle(SleeveStyle.entries, armour.sleeveStyle)
    editArmourStyle(state, armour.style)
}

// parse

fun parseBodyArmour(parameters: Parameters) = BodyArmour(
    parseArmourStyle(parameters),
    parse(parameters, LENGTH, OuterwearLength.Hip),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
    parseArmorStats(parameters),
)
