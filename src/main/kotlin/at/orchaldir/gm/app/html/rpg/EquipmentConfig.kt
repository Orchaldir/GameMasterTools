package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.statistic.parseOptionalStatisticId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.EquipmentConfig
import at.orchaldir.gm.core.selector.util.sortStatistics
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h3

// show

fun HtmlBlockTag.showEquipmentConfig(
    call: ApplicationCall,
    state: State,
    config: EquipmentConfig,
) {
    h3 { +"Equipment" }

    showSimpleModifiedDiceRange("Damage Modifier", config.damageModifier)
    field("Max Damage Resistance", config.maxDamageResistance)
    fieldRange("Damage Resistance Modifier", config.damageResistanceModifier)
    field("Max Defense Bonus", config.maxDefenseBonus)
    fieldRange("Defense Bonus Modifier", config.defenseBonusModifier)
    optionalFieldLink("Muscle-Powered Statistic", call, state, config.musclePoweredStatistic)
    fieldRange("Parrying Modifier", config.parryingModifier)
    fieldRange("Skill Modifier", config.skillModifier)
}


// edit

fun HtmlBlockTag.editEquipmentConfig(
    state: State,
    config: EquipmentConfig,
) {
    h3 { +"Equipment" }

    editSimpleModifiedDiceRange("Damage Modifier", config.damageModifier, combine(DAMAGE, MODIFIER))
    selectInt(
        "Max Damage Resistance",
        config.maxDamageResistance,
        1,
        100,
        1,
        combine(DAMAGE, RESISTANCE),
    )
    editRange(
        "Damage Resistance Modifier",
        config.damageResistanceModifier,
        combine(DAMAGE, RESISTANCE, MODIFIER),
    )
    selectInt(
        "Max Defense Bonus",
        config.maxDefenseBonus,
        1,
        100,
        1,
        DEFENSE,
    )
    editRange(
        "Defense Bonus Modifier",
        config.defenseBonusModifier,
        combine(DEFENSE, MODIFIER),
    )
    selectOptionalElement(
        state,
        "Muscle-Powered Statistic",
        STATISTIC,
        state.sortStatistics(),
        config.musclePoweredStatistic,
    )
    editRange(
        "Parrying Modifier",
        config.parryingModifier,
        combine(PARRYING, MODIFIER),
    )
    editRange(
        "Skill Modifier",
        config.skillModifier,
        combine(STATISTIC, MODIFIER),
    )
}

// parse

fun parseEquipmentConfig(
    parameters: Parameters,
) = EquipmentConfig(
    parseSimpleModifiedDiceRange(parameters, combine(DAMAGE, MODIFIER)),
    parseInt(parameters, combine(DAMAGE, RESISTANCE)),
    parseRange(parameters, combine(DAMAGE, RESISTANCE, MODIFIER)),
    parseInt(parameters, DEFENSE),
    parseRange(parameters, combine(DEFENSE, MODIFIER)),
    parseOptionalStatisticId(parameters, STATISTIC),
    parseRange(parameters, combine(PARRYING, MODIFIER)),
    parseRange(parameters, combine(STATISTIC, MODIFIER)),
)
