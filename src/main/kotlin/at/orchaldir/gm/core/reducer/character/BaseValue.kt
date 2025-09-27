package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.*
import at.orchaldir.gm.utils.doNothing

fun validateBaseValue(
    state: State,
    statistic: StatisticId,
    value: BaseValue,
) {
    when (value) {

        is FixedNumber -> doNothing()
        is BasedOnStatistic -> {
            state.getStatisticStorage().require(value.statistic)
            require(statistic != value.statistic) { "${statistic.print()} cannot be based on itself!" }
        }

        is DivisionOfValues -> {
            validateBaseValue(state, statistic, value.dividend)
            validateBaseValue(state, statistic, value.divisor)
        }

        is ProductOfValues -> validateBaseValues(state, statistic, value.values)
        is SumOfValues -> validateBaseValues(state, statistic, value.values)
    }
}

private fun validateBaseValues(
    state: State,
    statistic: StatisticId,
    values: List<BaseValue>,
) {
    require(values.size > 1) { "Requires at least 2 values!" }
    values.map { validateBaseValue(state, statistic, it) }
}
