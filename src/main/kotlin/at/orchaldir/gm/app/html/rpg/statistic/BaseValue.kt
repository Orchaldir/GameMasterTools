package at.orchaldir.gm.app.html.rpg.statistic

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.statistic.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag
import kotlin.math.absoluteValue

// show

fun HtmlBlockTag.showBaseValue(
    call: ApplicationCall,
    state: State,
    value: BaseValue,
    label: String = "Base Value",
) {
    field(label) {
        displayBaseValue(call, state, value)
    }
}

fun HtmlBlockTag.displayBaseValue(
    call: ApplicationCall,
    state: State,
    value: BaseValue,
    isTopLevel: Boolean = true,
) {
    when (value) {
        is BasedOnStatistic -> {
            if (value.offset != 0) {
                brackets(isTopLevel) {
                    link(call, state, value.statistic)
                    if (value.offset > 0) {
                        +" + ${value.offset}"
                    } else {
                        +" - ${value.offset.absoluteValue}"
                    }
                }
            } else {
                link(call, state, value.statistic)
            }
        }

        is FixedNumber -> +"${value.default}"
        is DivisionOfValues -> brackets(isTopLevel) {
            displayBaseValue(call, state, value.dividend, false)
            +" / "
            displayBaseValue(call, state, value.divisor, false)
        }

        is ProductOfValues -> displayValues(call, state, value.values, isTopLevel, "*")
        is SumOfValues -> displayValues(call, state, value.values, isTopLevel, "*")
    }
}

private fun HtmlBlockTag.displayValues(
    call: ApplicationCall,
    state: State,
    values: List<BaseValue>,
    isTopLevel: Boolean,
    sign: String,
) {
    brackets(isTopLevel) {
        values.withIndex().forEach { (i, subValue) ->
            if (i > 0) {
                +" $sign "
            }
            displayBaseValue(call, state, subValue, false)
        }
    }
}

private fun HtmlBlockTag.brackets(
    isTopLevel: Boolean,
    content: HtmlBlockTag.() -> Unit,
) {
    if (isTopLevel) {
        content()
    } else {
        +"("
        content()
        +")"
    }
}

// edit

fun HtmlBlockTag.editBaseValue(
    state: State,
    statistic: StatisticId,
    value: BaseValue,
    label: String = "Base Value",
    param: String = BASE,
) {
    val statistics = state.getStatisticStorage().getAllExcept(statistic)

    showDetails(label, true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            BaseValueType.entries,
            value.getType(),
        ) { type ->
            when (type) {
                BaseValueType.BasedOnStatistic -> statistics.isEmpty()
                BaseValueType.FixedNumber -> false
                BaseValueType.Division -> statistics.isEmpty()
                BaseValueType.Product -> statistics.isEmpty()
                BaseValueType.Sum -> statistics.isEmpty()
            }
        }

        when (value) {
            is BasedOnStatistic -> {
                selectElement(
                    state,
                    combine(param, REFERENCE),
                    statistics,
                    value.statistic,
                )
                selectOffset(value.offset, "Offset", param)
            }

            is FixedNumber -> selectOffset(value.default, "Default", param)

            is DivisionOfValues -> {
                editBaseValue(state, statistic, value.dividend, "Dividend", combine(param, DIVIDEND))
                editBaseValue(state, statistic, value.divisor, "Divisor", combine(param, DIVISOR))
            }

            is ProductOfValues -> editListOfValues(state, statistic, value.values, param)
            is SumOfValues -> editListOfValues(state, statistic, value.values, param)
        }
    }
}

private fun DETAILS.selectOffset(
    offset: Int,
    label: String,
    param: String,
) {
    selectInt(
        label,
        offset,
        -100,
        100,
        1,
        combine(param, NUMBER),
    )
}

private fun DETAILS.editListOfValues(
    state: State,
    statistic: StatisticId,
    values: List<BaseValue>,
    param: String,
) {
    editList(
        combine(param, LIST),
        values,
        2,
        10,
        1,
    ) { i, subParam, subValue ->
        editBaseValue(state, statistic, subValue, "${i + 1}.Value", subParam)
    }
}

// parse

fun parseBaseValue(
    parameters: Parameters,
    param: String = BASE,
): BaseValue = when (parse(parameters, combine(param, TYPE), BaseValueType.FixedNumber)) {
    BaseValueType.BasedOnStatistic -> BasedOnStatistic(
        parseStatisticId(parameters, combine(param, REFERENCE)),
        parseInt(parameters, combine(param, NUMBER), 0),
    )

    BaseValueType.FixedNumber -> FixedNumber(
        parseInt(parameters, combine(param, NUMBER), 0),
    )

    BaseValueType.Division -> DivisionOfValues(
        parseBaseValue(parameters, combine(param, DIVIDEND)),
        parseBaseValue(parameters, combine(param, DIVISOR)),
    )

    BaseValueType.Product -> ProductOfValues(
        parseValues(parameters, param),
    )

    BaseValueType.Sum -> SumOfValues(
        parseValues(parameters, param),
    )
}

private fun parseValues(
    parameters: Parameters,
    param: String,
) = parseList(parameters, combine(param, LIST), 2) { _, subParam ->
    parseBaseValue(parameters, subParam)
}
