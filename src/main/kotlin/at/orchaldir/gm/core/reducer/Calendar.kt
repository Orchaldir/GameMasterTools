package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateCalendar
import at.orchaldir.gm.core.action.DeleteCalendar
import at.orchaldir.gm.core.action.UpdateCalendar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.ImprovedCalendar
import at.orchaldir.gm.core.model.calendar.OriginalCalendar
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CALENDAR: Reducer<CreateCalendar, State> = { state, _ ->
    val calendar = Calendar(state.calendars.nextId)

    noFollowUps(state.copy(calendars = state.calendars.add(calendar)))
}

val DELETE_CALENDAR: Reducer<DeleteCalendar, State> = { state, action ->
    state.calendars.require(action.id)
    require(state.canDelete(action.id)) { "Calendar ${action.id.value} is used" }

    noFollowUps(state.copy(calendars = state.calendars.remove(action.id)))
}

val UPDATE_CALENDAR: Reducer<UpdateCalendar, State> = { state, action ->
    val calendar = action.calendar

    state.calendars.require(calendar.id)
    checkMonths(calendar)
    checkOrigin(state, calendar)

    noFollowUps(state.copy(calendars = state.calendars.update(calendar)))
}

private fun checkMonths(
    calendar: Calendar,
) {
    require(calendar.months.size > 1) { "Requires at least 2 months" }
    calendar.months.forEach { require(it.days > 1) { "Requires at least 2 days per month" } }
    require(calendar.months.map { it.name }.toSet().size == calendar.months.size) {
        "The names of the months need to be unique!"
    }
}

private fun checkOrigin(
    state: State,
    calendar: Calendar,
) {
    when (val origin = calendar.origin) {
        is ImprovedCalendar -> {
            require(state.calendars.contains(origin.parent)) { "Parent calendar must exist!" }
            require(origin.parent != calendar.id) { "Calendar cannot be its own parent!" }
        }

        OriginalCalendar -> doNothing()
    }
}
