package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.DAMAGE
import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.DieType
import at.orchaldir.gm.core.model.rpg.RpgConfig
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showRpgConfig(
    call: ApplicationCall,
    state: State,
    config: RpgConfig,
) {
    h2 { +"RPG" }

    field("Default Die Type", config.defaultDieType)
    showSimpleModifiedDiceRange("Damage", config.damage)

    showEquipmentConfig(call, state, config.equipment)
}


// edit

fun HtmlBlockTag.editRpgConfig(
    state: State,
    config: RpgConfig,
) {
    h2 { +"RPG" }

    selectValue("Default Die Type", DIE, DieType.entries, config.defaultDieType)
    editSimpleModifiedDiceRange("Damage", config.damage, DAMAGE)

    editEquipmentConfig(state, config.equipment)
}

// parse

fun parseRpgConfig(
    parameters: Parameters,
) = RpgConfig(
    parseEquipmentConfig(parameters),
    parse(parameters, DIE, DieType.D6),
    parseSimpleModifiedDiceRange(parameters, DAMAGE),
)
