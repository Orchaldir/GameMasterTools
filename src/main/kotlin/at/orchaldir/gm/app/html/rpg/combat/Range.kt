package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.statistic.parseStatisticId
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.selector.util.sortStatistics
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.DOUBLE
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.TEN_PERCENTS
import at.orchaldir.gm.utils.math.ZERO
import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldRange(
    call: ApplicationCall,
    range: Range,
) {
    field("Range") {
        displayRange(call, range, true)
    }
}

fun HtmlBlockTag.displayRange(
    call: ApplicationCall,
    range: Range,
    showUndefined: Boolean = false,
) {
    when (range) {
        is FixedHalfAndMaxRange -> +"${range.half}/${range.max}"
        is StatisticBasedHalfAndMaxRange -> link(call, range.statistic, "x${range.half}/x${range.max}")
        UndefinedRange -> if(showUndefined) {
            +"Undefined"
        }

    }
}

// edit

fun HtmlBlockTag.editRange(
    state: State,
    range: Range,
    param: String,
) {
    val rangeParam = combine(param, RANGE)

    showDetails("Range", true) {
        selectValue(
            "Type",
            combine(rangeParam, TYPE),
            RangeType.entries,
            range.getType(),
        )

        when (range) {
            is FixedHalfAndMaxRange -> {
                selectInt(
                    "Half",
                    range.half,
                    0,
                    range.max - 1,
                    1,
                    combine(rangeParam, MIN),
                )
                selectInt(
                    "Max",
                    range.max,
                    range.half + 1,
                    10000,
                    1,
                    combine(rangeParam, MAX),
                )
            }
            is StatisticBasedHalfAndMaxRange -> {
                selectElement(
                    state,
                    "Attribute",
                    combine(rangeParam, STATISTIC),
                    state.sortStatistics(),
                    range.statistic,
                )
                selectFactor(
                    "Half",
                    combine(rangeParam, MIN),
                    range.half,
                    ZERO,
                    range.max - TEN_PERCENTS,
                    TEN_PERCENTS,
                )
                selectFactor(
                    "Max",
                    combine(rangeParam, MAX),
                    range.max,
                    range.half + TEN_PERCENTS,
                    Factor.fromNumber(10),
                    TEN_PERCENTS,
                )
            }

            UndefinedRange -> doNothing()
        }
    }
}

// parse

fun parseRange(
    parameters: Parameters,
    param: String,
): Range {
    val rangeParam = combine(param, RANGE)

    return when (parse(parameters, combine(rangeParam, TYPE), RangeType.Undefined)) {
        RangeType.FixedHalfAndMax -> FixedHalfAndMaxRange(
            parseInt(parameters, combine(rangeParam, MIN), 100),
            parseInt(parameters, combine(rangeParam, MAX), 200),
        )

        RangeType.StatisticBasedHalfAndMax -> StatisticBasedHalfAndMaxRange(
            parseStatisticId(parameters, combine(rangeParam, STATISTIC)),
            parseFactor(parameters, combine(rangeParam, MIN), ONE),
            parseFactor(parameters, combine(rangeParam, MAX), DOUBLE),
        )

        RangeType.Undefined -> UndefinedRange
    }
}
