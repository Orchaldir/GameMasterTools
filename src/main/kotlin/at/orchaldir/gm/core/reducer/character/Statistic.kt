package at.orchaldir.gm.core.reducer.character

import at.orchaldir.gm.core.action.CreateStatistic
import at.orchaldir.gm.core.action.UpdateStatistic
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.statistic.Statistic
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
}
