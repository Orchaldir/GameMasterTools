package at.orchaldir.gm.app.html.character.statistic

import at.orchaldir.gm.app.BASE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBaseValue(
    call: ApplicationCall,
    state: State,
    value: BaseValue,
) {
    showDetails("Base Value", true) {
        field("Type", value.getType())

        when (value) {
            is FixedNumber -> field("Default", value.default)
            is BasedOnStatistic -> {
                fieldLink(call, state, value.statistic)
                field("Offset", value.offset)
            }
        }
    }
}

// edit

fun FORM.editBaseValue(
    state: State,
    statistic: StatisticId,
    value: BaseValue,
    param: String = BASE,
) {
    val statistics = state.getStatisticStorage().getAllExcept(statistic)

    showDetails("Base Value", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            BaseValueType.entries,
            value.getType(),
        ) { type ->
            when (type) {
                BaseValueType.FixedNumber -> false
                BaseValueType.BasedOnStatistic -> statistics.isEmpty()
            }
        }

        when (value) {
            is FixedNumber -> selectInt(
                "Default",
                value.default,
                -100,
                100,
                1,
                combine(param, NUMBER),
            )

            is BasedOnStatistic -> {
                selectElement(
                    state,
                    combine(param, REFERENCE),
                    statistics,
                    value.statistic,
                )
                selectInt(
                    "Offset",
                    value.offset,
                    -100,
                    100,
                    1,
                    combine(param, NUMBER),
                )
            }
        }
    }
}

// parse

fun parseBaseValue(
    parameters: Parameters,
    param: String = BASE,
) = when (parse(parameters, combine(param, TYPE), BaseValueType.FixedNumber)) {
    BaseValueType.FixedNumber -> FixedNumber(
        parseInt(parameters, combine(param, NUMBER), 0),
    )

    BaseValueType.BasedOnStatistic -> BasedOnStatistic(
        parseStatisticId(parameters, combine(param, REFERENCE)),
        parseInt(parameters, combine(param, NUMBER), 0),
    )
}
