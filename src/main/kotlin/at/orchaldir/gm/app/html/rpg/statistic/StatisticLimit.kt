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
    limit: StatisticLimit,
    label: String,
) {
    field(label) {
        when (limit) {
            is AbsoluteStatisticLimit -> displayRange(limit.range)
            is RelativeStatisticLimit -> displayRange(limit.range)
            UndefinedStatisticLimit ->  +"None"
        }
    }

}

// edit

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
            UndefinedStatisticLimit -> doNothing()
        }
    }
}

// parse

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
}
