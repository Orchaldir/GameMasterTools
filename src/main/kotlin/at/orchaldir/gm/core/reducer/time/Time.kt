package at.orchaldir.gm.core.reducer.time

import at.orchaldir.gm.core.action.UpdateTime
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_TIME: Reducer<UpdateTime, State> = { state, action ->
    state.getCalendarStorage().require(action.time.defaultCalendar)

    noFollowUps(state.copy(time = action.time))
}