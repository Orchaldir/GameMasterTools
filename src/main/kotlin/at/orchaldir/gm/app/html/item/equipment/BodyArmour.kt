package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.SLEEVE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.editArmour
import at.orchaldir.gm.app.html.item.equipment.style.parseArmour
import at.orchaldir.gm.app.html.item.equipment.style.selectSleeveStyle
import at.orchaldir.gm.app.html.item.equipment.style.showArmour
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SleeveStyle
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBodyArmour(
    call: ApplicationCall,
    state: State,
    armour: BodyArmour,
) {
    field("Length", armour.length)
    field("Sleeve Style", armour.sleeveStyle)
    showArmour(call, state, armour.style)
}

// edit

fun FORM.editBodyArmour(
    state: State,
    armour: BodyArmour,
) {
    selectValue("Length", LENGTH, OuterwearLength.entries, armour.length)
    selectSleeveStyle(SleeveStyle.entries, armour.sleeveStyle)
    editArmour(state, armour.style)
}

// parse

fun parseBodyArmour(parameters: Parameters) = BodyArmour(
    parseArmour(parameters),
    parse(parameters, LENGTH, OuterwearLength.Hip),
    parse(parameters, combine(SLEEVE, STYLE), SleeveStyle.Long),
)
