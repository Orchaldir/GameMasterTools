package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.CreatePeriodical
import at.orchaldir.gm.core.action.DeletePeriodical
import at.orchaldir.gm.core.action.UpdatePeriodical
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.*
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.selector.item.canDeletePeriodical
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_PERIODICAL: Reducer<CreatePeriodical, State> = { state, _ ->
    val periodical = Periodical(state.getPeriodicalStorage().nextId)

    noFollowUps(state.updateStorage(state.getPeriodicalStorage().add(periodical)))
}

val DELETE_PERIODICAL: Reducer<DeletePeriodical, State> = { state, action ->
    state.getPeriodicalStorage().require(action.id)
    require(state.canDeletePeriodical(action.id)) { "The periodical ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getPeriodicalStorage().remove(action.id)))
}

val UPDATE_PERIODICAL: Reducer<UpdatePeriodical, State> = { state, action ->
    state.getPeriodicalStorage().require(action.periodical.id)
    checkDate(state, action.periodical.startDate(), "Periodical")

    noFollowUps(state.updateStorage(state.getPeriodicalStorage().update(action.periodical)))
}