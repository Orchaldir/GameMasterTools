package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.statistic.parseOptionalStatisticId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.DieType
import at.orchaldir.gm.core.model.rpg.RpgData
import at.orchaldir.gm.core.selector.util.sortStatistics
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
    showSimpleModifiedDiceRange("Damage Modifier", data.damageModifier)
    field("Max Damage Resistance", data.maxDamageResistance)
    fieldRange("Damage Resistance Modifier", data.damageResistanceModifier)
    field("Max Defense Bonus", data.maxDefenseBonus)
    fieldRange("Defense Bonus Modifier", data.defenseBonusModifier)
    optionalFieldLink("Muscle-Powered Statistic", call, state, data.musclePoweredStatistic)
}


// edit

fun HtmlBlockTag.editRpgData(
    state: State,
    data: RpgData,
) {
    h2 { +"RPG" }

    selectValue("Default Die Type", DIE, DieType.entries, data.defaultDieType)
    editSimpleModifiedDiceRange("Damage", data.damage, DAMAGE)
    editSimpleModifiedDiceRange("Damage Modifier", data.damageModifier, combine(DAMAGE, MODIFIER))
    selectInt(
        "Max Damage Resistance",
        data.maxDamageResistance,
        1,
        100,
        1,
        combine(DAMAGE, RESISTANCE),
    )
    editRange(
        "Damage Resistance Modifier",
        data.damageResistanceModifier,
        combine(DAMAGE, RESISTANCE, MODIFIER),
    )
    selectInt(
        "Max Defense Bonus",
        data.maxDefenseBonus,
        1,
        100,
        1,
        DEFENSE,
    )
    editRange(
        "Defense Bonus Modifier",
        data.defenseBonusModifier,
        combine(DEFENSE, MODIFIER),
    )
    selectOptionalElement(
        state,
        "Muscle-Powered Statistic",
        STATISTIC,
        state.sortStatistics(),
        data.musclePoweredStatistic,
    )
}

// parse

fun parseRpgData(
    parameters: Parameters,
) = RpgData(
    parse(parameters, DIE, DieType.D6),
    parseSimpleModifiedDiceRange(parameters, DAMAGE),
    parseSimpleModifiedDiceRange(parameters, combine(DAMAGE, MODIFIER)),
    parseInt(parameters, combine(DAMAGE, RESISTANCE)),
    parseRange(parameters, combine(DAMAGE, RESISTANCE, MODIFIER)),
    parseInt(parameters, DEFENSE),
    parseRange(parameters, combine(DEFENSE, MODIFIER)),
    parseOptionalStatisticId(parameters, STATISTIC),
)
