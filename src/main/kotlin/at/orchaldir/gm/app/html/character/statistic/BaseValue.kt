package at.orchaldir.gm.app.html.character.statistic

import at.orchaldir.gm.app.BASE
import at.orchaldir.gm.app.DIVIDEND
import at.orchaldir.gm.app.DIVISOR
import at.orchaldir.gm.app.LIST
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
    showDetails(label, true) {
        field("Type", value.getType())

        when (value) {
            is BasedOnStatistic -> {
                fieldLink(call, state, value.statistic)
                field("Offset", value.offset)
            }
            is FixedNumber -> field("Default", value.default)
            is DivisionOfValues -> {
                showBaseValue(call, state, value.dividend, "Dividend")
                showBaseValue(call, state, value.divisor, "Divisor")
            }
            is ProductOfValues -> value.values.withIndex().forEach { (i, subValue) ->
                showBaseValue(call, state, subValue, "${i+1}.Value")
            }
            is SumOfValues -> value.values.withIndex().forEach { (i, subValue) ->
                showBaseValue(call, state, subValue, "${i+1}.Value")
            }
        }
    }
}

fun HtmlBlockTag.displayBaseValue(
    call: ApplicationCall,
    state: State,
    value: BaseValue,
) {
    when (value) {
        is BasedOnStatistic -> {
            if (value.offset != 0) {
                brackets {
                    link(call, state, value.statistic)
                    if (value.offset > 0) {
                        +" + ${value.offset}"
                    }
                    else {
                        +" - ${value.offset.absoluteValue}"
                    }
                }
            }
            else {
                link(call, state, value.statistic)
            }
        }
        is FixedNumber -> +"${value.default}"
        is DivisionOfValues -> brackets {
            displayBaseValue(call, state, value.dividend)
            +" / "
            displayBaseValue(call, state, value.divisor)
        }
        is ProductOfValues -> displayValues(call, state, value.values, "*")
        is SumOfValues -> displayValues(call, state, value.values, "*")
    }
}

private fun HtmlBlockTag.displayValues(
    call: ApplicationCall,
    state: State,
    values: List<BaseValue>,
    sign: String,
) {
    brackets {
        values.withIndex().forEach { (i, subValue) ->
            if (i > 0) {
                +" $sign "
            }
            displayBaseValue(call, state, subValue)
        }
    }
}

private fun HtmlBlockTag.brackets(
    content: HtmlBlockTag.() -> Unit,
) {
    +"( "
    content()
    +")"
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
