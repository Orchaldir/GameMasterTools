package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.statistic.parseStatisticId
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.rpg.statistic.StatisticId
import at.orchaldir.gm.core.selector.util.sortStatistics
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldRange(
    call: ApplicationCall,
    state: State,
    range: Range,
) {
    field("Range") {
        displayRange(call, state, range, true)
    }
}

fun HtmlBlockTag.displayRange(
    call: ApplicationCall,
    state: State,
    range: Range,
    showUndefined: Boolean = false,
) {
    when (range) {
        is FixedHalfAndMaxRange -> +"${range.half}/${range.max}"
        is MusclePoweredHalfAndMaxRange -> if (state.data.rpg.musclePoweredStatistic != null) {
            displayHalfAndMaxRange(call, range.half, range.max, state.data.rpg.musclePoweredStatistic)
        } else {
            error("Muscle-Powered Half And Max Range is not supported!")
        }

        is StatisticBasedHalfAndMaxRange -> displayHalfAndMaxRange(call, range.half, range.max, range.statistic)
        UndefinedRange -> if (showUndefined) {
            +"Undefined"
        }

    }
}

private fun HtmlBlockTag.displayHalfAndMaxRange(
    call: ApplicationCall,
    half: Factor,
    max: Factor,
    statistic: StatisticId,
) {
    val text = "x${half.toStringAsNumber()}/x${max.toStringAsNumber()}"
    link(call, statistic, text)
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
        ) { type ->
            when (type) {
                RangeType.FixedHalfAndMax -> false
                RangeType.MusclePoweredHalfAndMax -> state.data.rpg.musclePoweredStatistic == null
                RangeType.StatisticBasedHalfAndMax -> state.getStatisticStorage().isEmpty()
                RangeType.Undefined -> false
            }
        }

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

            is MusclePoweredHalfAndMaxRange -> editHalfAndMaxRange(rangeParam, range.half, range.max)
            is StatisticBasedHalfAndMaxRange -> {
                selectElement(
                    state,
                    "Attribute",
                    combine(rangeParam, STATISTIC),
                    state.sortStatistics(),
                    range.statistic,
                )
                editHalfAndMaxRange(rangeParam, range.half, range.max)
            }

            UndefinedRange -> doNothing()
        }
    }
}

private fun DETAILS.editHalfAndMaxRange(
    param: String,
    half: Factor,
    max: Factor,
) {
    selectFactor(
        "Half",
        combine(param, MIN),
        half,
        ZERO,
        max - TEN_PERCENTS,
        TEN_PERCENTS,
    )
    selectFactor(
        "Max",
        combine(param, MAX),
        max,
        half + TEN_PERCENTS,
        Factor.fromNumber(30),
        TEN_PERCENTS,
    )
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

        RangeType.MusclePoweredHalfAndMax -> MusclePoweredHalfAndMaxRange(
            parseMinRange(parameters, rangeParam),
            parseMaxRange(parameters, rangeParam),
        )

        RangeType.StatisticBasedHalfAndMax -> StatisticBasedHalfAndMaxRange(
            parseStatisticId(parameters, combine(rangeParam, STATISTIC)),
            parseMinRange(parameters, rangeParam),
            parseMaxRange(parameters, rangeParam),
        )

        RangeType.Undefined -> UndefinedRange
    }
}

private fun parseMinRange(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, MIN), ONE)

private fun parseMaxRange(parameters: Parameters, param: String) =
    parseFactor(parameters, combine(param, MAX), DOUBLE)
