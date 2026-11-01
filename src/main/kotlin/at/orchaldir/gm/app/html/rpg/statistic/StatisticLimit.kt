package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
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
        when (limit) {
            is AbsoluteStatisticLimit -> {
                displayRange(limit.range)
                +" (Absolute)"
            }
            is RelativeStatisticLimit -> {
                displayRange(limit.range)
                +" (Relative)"
            }
            UndefinedStatisticLimit ->  +"None"
            UnmodifiableStatistic -> +"Unmodifiable"
        }
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
    StatisticLimitType.Undefined -> UndefinedStatisticLimit
    StatisticLimitType.Unmodifiable -> UnmodifiableStatistic
}
