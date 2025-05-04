package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.UpdateData
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_DATA: Reducer<UpdateData, State> = { state, action ->
    state.getCalendarStorage().require(action.data.time.defaultCalendar)
    state.getCurrencyStorage().require(action.data.economy.defaultCurrency)

    noFollowUps(state.copy(data = action.data))
}