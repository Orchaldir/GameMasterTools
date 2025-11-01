package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.COST
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.rpg.statistic.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldStatisticCost(
    cost: StatisticCost,
) {
    field("Cost") {
        displayStatisticCost(cost)
    }
}

fun HtmlBlockTag.displayStatisticCost(
    cost: StatisticCost,
    showUndefined: Boolean = true,
) {
    when (cost) {
        is FixedStatisticCost -> +"${cost.cost} per Point"
        GurpsSkillCost -> +"Gurps Skill Cost"
        UndefinedStatisticCost -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// edit

fun HtmlBlockTag.editStatisticCost(
    cost: StatisticCost,
) {
    showDetails("Cost", true) {
        selectValue(
            "Type",
            combine(COST, TYPE),
            StatisticCostType.entries,
            cost.getType(),
        )

        when (cost) {
            is FixedStatisticCost -> selectInt(
                "Cost per Point",
                cost.cost,
                1,
                100,
                1,
                combine(COST, NUMBER),
            )

            GurpsSkillCost -> doNothing()
            UndefinedStatisticCost -> doNothing()
        }
    }
}

// parse

fun parseStatisticCost(
    parameters: Parameters,
) = when (parse(parameters, combine(COST, TYPE), StatisticCostType.Undefined)) {
    StatisticCostType.Fixed -> FixedStatisticCost(
        parseInt(parameters, combine(COST, NUMBER), 1),
    )

    StatisticCostType.GurpsSkill -> GurpsSkillCost
    StatisticCostType.Undefined -> UndefinedStatisticCost
}
