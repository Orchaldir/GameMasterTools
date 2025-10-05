package at.orchaldir.gm.app.html.character.statistic

import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStatisticData(
    call: ApplicationCall,
    state: State,
    data: StatisticData,
) {
    field("Type", data.getType())

    when (data) {
        is Attribute -> {
            showBaseValue(call, state, data.base)
            fieldStatisticCost(data.cost)
        }

        is BaseDamage -> {
            showBaseValue(call, state, data.base)
            fieldStatisticCost(data.cost)
            showBaseDamageLookup(data.lookup)
        }

        is DerivedAttribute -> {
            showBaseValue(call, state, data.base)
            fieldStatisticCost(data.cost)
            fieldStatisticUnit(data.unit)
        }

        is Skill -> {
            showBaseValue(call, state, data.base)
            fieldStatisticCost(data.cost)
        }
    }
}

// edit

fun FORM.editStatisticData(
    state: State,
    statistic: StatisticId,
    data: StatisticData,
) {
    selectValue(
        "Type",
        TYPE,
        StatisticDataType.entries,
        data.getType(),
    )

    when (data) {
        is Attribute -> {
            editBaseValue(state, statistic, data.base)
            editStatisticCost(data.cost)
        }

        is BaseDamage -> {
            editBaseValue(state, statistic, data.base)
            editStatisticCost(data.cost)
            editBaseDamageLookup(data.lookup)
        }

        is DerivedAttribute -> {
            editBaseValue(state, statistic, data.base)
            editStatisticCost(data.cost)
            editStatisticUnit(data.unit)
        }

        is Skill -> {
            editBaseValue(state, statistic, data.base)
            editStatisticCost(data.cost)
        }
    }
}

// parse

fun parseStatisticData(
    parameters: Parameters,
) = when (parse(parameters, TYPE, StatisticDataType.Attribute)) {
    StatisticDataType.Attribute -> Attribute(
        parseBaseValue(parameters),
        parseStatisticCost(parameters),
    )

    StatisticDataType.Damage -> BaseDamage(
        parseBaseValue(parameters),
        parseStatisticCost(parameters),
        parseBaseDamageLookup(parameters),
    )

    StatisticDataType.DerivedAttribute -> DerivedAttribute(
        parseBaseValue(parameters),
        parseStatisticCost(parameters),
        parseStatisticUnit(parameters),
    )

    StatisticDataType.Skill -> Skill(
        parseBaseValue(parameters),
        parseStatisticCost(parameters),
    )
}
