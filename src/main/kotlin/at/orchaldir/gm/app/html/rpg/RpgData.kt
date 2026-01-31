package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.DAMAGE
import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.DieType
import at.orchaldir.gm.core.model.rpg.RpgData
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showRpgData(
    call: ApplicationCall,
    state: State,
    data: RpgData,
) {
    h2 { +"RPG" }

    field("Default Die Type", data.defaultDieType)
    showSimpleModifiedDiceRange("Damage", data.damage)

    showEquipmentData(call, state, data.equipment)
}


// edit

fun HtmlBlockTag.editRpgData(
    state: State,
    data: RpgData,
) {
    h2 { +"RPG" }

    selectValue("Default Die Type", DIE, DieType.entries, data.defaultDieType)
    editSimpleModifiedDiceRange("Damage", data.damage, DAMAGE)

    editEquipmentData(state, data.equipment)
}

// parse

fun parseRpgData(
    parameters: Parameters,
) = RpgData(
    parseEquipmentData(parameters),
    parse(parameters, DIE, DieType.D6),
    parseSimpleModifiedDiceRange(parameters, DAMAGE),
)
