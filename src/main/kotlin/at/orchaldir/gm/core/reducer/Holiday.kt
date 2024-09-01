package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateHoliday
import at.orchaldir.gm.core.action.DeleteHoliday
import at.orchaldir.gm.core.action.UpdateHoliday
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_HOLIDAY: Reducer<CreateHoliday, State> = { state, _ ->
    val holiday = Holiday(state.getHolidayStorage().nextId)

    noFollowUps(state.updateStorage(state.getHolidayStorage().add(holiday)))
}

val DELETE_HOLIDAY: Reducer<DeleteHoliday, State> = { state, action ->
    state.getHolidayStorage().require(action.id)
    require(state.canDelete(action.id)) { "Holiday ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getHolidayStorage().remove(action.id)))
}

val UPDATE_HOLIDAY: Reducer<UpdateHoliday, State> = { state, action ->
    val holiday = action.holiday

    state.getHolidayStorage().require(holiday.id)

    noFollowUps(state.updateStorage(state.getHolidayStorage().update(holiday)))
}