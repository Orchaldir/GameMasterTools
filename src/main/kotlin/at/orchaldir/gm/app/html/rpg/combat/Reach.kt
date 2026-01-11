package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldReach(
    reach: Reach,
) {
    field("Reach") {
        displayReach(reach)
    }
}

fun HtmlBlockTag.displayReach(
    reach: Reach,
) {
    when (reach) {
        is SimpleReach -> +"${reach.distance}"
        is ReachRange -> {
            +"${reach.min}-${reach.max}"

            if (reach.changeRequiresEffort) {
                +"*"
            }
        }

        UndefinedReach -> +"Undefined"
    }
}

// edit

fun HtmlBlockTag.editReach(
    reach: Reach,
    param: String,
) {
    val reachParam = combine(param, REACH)
    val maxReach = 10

    showDetails("Reach", true) {
        selectValue(
            "Type",
            combine(reachParam, TYPE),
            ReachType.entries,
            reach.getType(),
        )

        when (reach) {
            is SimpleReach -> selectInt(
                "Distance",
                reach.distance,
                0,
                maxReach,
                1,
                combine(reachParam, NUMBER),
            )

            is ReachRange -> {
                selectInt(
                    "Min",
                    reach.min,
                    0,
                    reach.max - 1,
                    1,
                    combine(reachParam, MIN),
                )
                selectInt(
                    "Max",
                    reach.max,
                    reach.min + 1,
                    maxReach,
                    1,
                    combine(reachParam, MAX),
                )
            }

            UndefinedReach -> doNothing()
        }
    }
}

// parse

fun parseReach(
    parameters: Parameters,
    param: String,
): Reach {
    val reachParam = combine(param, REACH)

    return when (parse(parameters, combine(reachParam, TYPE), ReachType.Undefined)) {
        ReachType.Simple -> SimpleReach(
            parseInt(parameters, combine(reachParam, NUMBER), 1),
        )

        ReachType.Range -> ReachRange(
            parseInt(parameters, combine(reachParam, MIN), 1),
            parseInt(parameters, combine(reachParam, MAX), 2),
        )

        ReachType.Undefined -> UndefinedReach
    }
}
