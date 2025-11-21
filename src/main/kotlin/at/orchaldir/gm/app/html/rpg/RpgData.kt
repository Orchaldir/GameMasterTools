package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.DAMAGE
import at.orchaldir.gm.app.DEFENSE
import at.orchaldir.gm.app.DIE
import at.orchaldir.gm.app.MODIFIER
import at.orchaldir.gm.app.RESISTANCE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
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
    showSimpleModifiedDiceRange("Damage Range", data.damageRange)
    showSimpleModifiedDiceRange("Damage Modifier Range", data.damageModifierRange)
    fieldRange("Damage Resistance", data.damageResistanceRange)
    fieldRange("Defense Bonus", data.defenseBonusRange)
}


// edit

fun HtmlBlockTag.editRpgData(
    state: State,
    data: RpgData,
) {
    h2 { +"RPG" }

    selectValue("Default Die Type", DIE, DieType.entries, data.defaultDieType)
    editSimpleModifiedDiceRange("Damage Range", data.damageRange, DAMAGE)
    editSimpleModifiedDiceRange("Damage Modifier Range", data.damageModifierRange, combine(DAMAGE, MODIFIER))
    editRange(
        "Damage Resistance",
        data.damageResistanceRange,
        combine(DAMAGE, RESISTANCE),
    )
    editRange(
        "Defense Bonus",
        data.defenseBonusRange,
        DEFENSE,
    )
}

// parse

fun parseRpgData(
    parameters: Parameters,
) = RpgData(
    parse(parameters, DIE, DieType.D6),
    parseSimpleModifiedDiceRange(parameters, DAMAGE),
    parseSimpleModifiedDiceRange(parameters, MODIFIER),
    parseRange(parameters, combine(DAMAGE, RESISTANCE)),
    parseRange(parameters, DEFENSE),
)
