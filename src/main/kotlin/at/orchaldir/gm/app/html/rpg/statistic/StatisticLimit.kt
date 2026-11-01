package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.MAX
import at.orchaldir.gm.app.MIN
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.DEFAULT_INT_RANGE
import at.orchaldir.gm.app.html.util.math.displayRange
import at.orchaldir.gm.app.html.util.math.editRange
import at.orchaldir.gm.app.html.util.math.parseRange
import at.orchaldir.gm.core.model.rpg.statistic.*
import at.orchaldir.gm.utils.doNothing
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
        UndefinedStatisticLimit -> if(showUndefined) {
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
            is RelativeStatisticLimit -> editRange(limit.range, param,)
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
    StatisticLimitType.Undefined -> UndefinedStatisticLimit
    StatisticLimitType.Unmodifiable -> UnmodifiableStatistic
}
