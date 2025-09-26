package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CreateStatistic
import at.orchaldir.gm.core.action.UpdateStatistic
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_STATISTIC: Reducer<CreateStatistic, State> = { state, _ ->
    val statistic = Statistic(state.getStatisticStorage().nextId)
    val statistics = state.getStatisticStorage().add(statistic)
    noFollowUps(state.updateStorage(statistics))
}

val UPDATE_STATISTIC: Reducer<UpdateStatistic, State> = { state, action ->
    val statistic = action.statistic
    state.getStatisticStorage().require(statistic.id)

    validateStatistic(state, statistic)

    noFollowUps(state.updateStorage(state.getStatisticStorage().update(statistic)))
}

fun validateStatistic(
    state: State,
    statistic: Statistic,
) {
    state.getDataSourceStorage().require(statistic.sources)

    when (statistic.data) {
        is Attribute -> validateBaseValue(state, statistic.id, statistic.data.base)
        is DerivedAttribute -> validateBaseValue(state, statistic.id, statistic.data.base)
        is Skill -> validateBaseValue(state, statistic.id, statistic.data.base)
    }
}

private fun validateBaseValue(
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
