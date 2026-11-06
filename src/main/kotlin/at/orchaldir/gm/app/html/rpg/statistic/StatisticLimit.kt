package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.*
import at.orchaldir.gm.core.model.rpg.statistic.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.RoundingType
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldStatisticLimit(
    label: String,
    limit: StatisticLimit,
) {
    field(label) {
        displayStatisticLimit(limit)
    }
}

fun HtmlBlockTag.displayStatisticLimit(
    limit: StatisticLimit,
    showUndefined: Boolean = true,
) {
    when (limit) {
        is AbsoluteStatisticLimit -> {
            displayRange(limit.range)
            +" (Absolute)"
        }

        is RelativeStatisticLimit -> {
            displayRange(limit.range)
            +" (Relative)"
        }

        is RelativeLowerAndAbsoluteUpperLimit -> +"${limit.lower} (Relative) to ${limit.upper} (Absolute)"
        is RelativeWithFactorStatisticLimit -> +"${limit.factor} of base"
        UndefinedStatisticLimit -> if (showUndefined) {
            +"Undefined"
        }

        UnmodifiableStatistic -> +"Unmodifiable"
    }
}

// edit

fun HtmlBlockTag.editStatisticLimit(
    label: String,
    limit: StatisticLimit,
    type: StatisticDataType,
    param: String,
) = editStatisticLimit(
    label,
    limit,
    combine(param, type),
)

fun HtmlBlockTag.editStatisticLimit(
    label: String,
    limit: StatisticLimit,
    param: String,
) {
    showDetails(label, true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            StatisticLimitType.entries,
            limit.getType(),
        )

        when (limit) {
            is AbsoluteStatisticLimit -> editRange(limit.range, param)
            is RelativeStatisticLimit -> editRange(limit.range, param)
            is RelativeLowerAndAbsoluteUpperLimit -> {
                selectInt(
                    "Relative Lower Limit",
                    limit.lower,
                    DEFAULT_INT_RANGE,
                    1,
                    combine(param, MIN),
                )
                selectInt(
                    "Absolute Upper Limit",
                    limit.upper,
                    DEFAULT_INT_RANGE,
                    1,
                    combine(param, MAX),
                )
            }

            is RelativeWithFactorStatisticLimit -> {
                selectFactor(
                    "Factor",
                    combine(param, NUMBER),
                    limit.factor,
                )
                selectValue(
                    "Rounding",
                    combine(param, ROUND),
                    RoundingType.entries,
                    limit.rounding,
                )
            }

            UndefinedStatisticLimit, UnmodifiableStatistic -> doNothing()
        }
    }
}

// parse

fun parseStatisticLimit(
    parameters: Parameters,
    type: StatisticDataType,
    param: String,
) = parseStatisticLimit(
    parameters,
    combine(param, type),
)

fun parseStatisticLimit(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, combine(param, TYPE), StatisticLimitType.Undefined)) {
    StatisticLimitType.Absolute -> AbsoluteStatisticLimit(
        parseRange(parameters, param),
    )

    StatisticLimitType.Relative -> RelativeStatisticLimit(
        parseRange(parameters, param),
    )

    StatisticLimitType.RelativeLowerAndAbsoluteUpper -> RelativeLowerAndAbsoluteUpperLimit(
        parseInt(parameters, combine(param, MIN)),
        parseInt(parameters, combine(param, MAX)),
    )

    StatisticLimitType.RelativeWithFactor -> RelativeWithFactorStatisticLimit(
        parseFactor(parameters, combine(param, NUMBER)),
        parse(parameters, combine(param, ROUND), RoundingType.RoundUp),
    )

    StatisticLimitType.Undefined -> UndefinedStatisticLimit
    StatisticLimitType.Unmodifiable -> UnmodifiableStatistic
}
