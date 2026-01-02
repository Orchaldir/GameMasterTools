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
    param: String = PARRYING,
) {
    val maxReach = 10

    showDetails("Reach", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
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
                combine(param, NUMBER),
            )

            is ReachRange -> {
                selectInt(
                    "Min",
                    reach.min,
                    0,
                    reach.max - 1,
                    1,
                    combine(param, MIN),
                )
                selectInt(
                    "Max",
                    reach.max,
                    reach.min + 1,
                    maxReach,
                    1,
                    combine(param, MAX),
                )
            }

            UndefinedReach -> doNothing()
        }
    }
}

// parse

fun parseReach(
    parameters: Parameters,
    param: String = PARRYING,
) = when (parse(parameters, combine(param, TYPE), ReachType.Undefined)) {
    ReachType.Simple -> SimpleReach(
        parseInt(parameters, combine(param, NUMBER), 1),
    )

    ReachType.Range -> ReachRange(
        parseInt(parameters, combine(param, MIN), 1),
        parseInt(parameters, combine(param, MAX), 2),
    )

    ReachType.Undefined -> UndefinedReach
}
