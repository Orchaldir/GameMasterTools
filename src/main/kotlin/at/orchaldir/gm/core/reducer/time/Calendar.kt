package at.orchaldir.gm.core.reducer.time

import at.orchaldir.gm.core.action.CreateCalendar
import at.orchaldir.gm.core.action.DeleteCalendar
import at.orchaldir.gm.core.action.UpdateCalendar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.*
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.time.calendar.canDelete
import at.orchaldir.gm.core.selector.time.getDefaultCalendarId
import at.orchaldir.gm.core.selector.time.getHolidays
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CALENDAR: Reducer<CreateCalendar, State> = { state, _ ->
    val calendar = Calendar(state.getCalendarStorage().nextId)

    noFollowUps(state.updateStorage(state.getCalendarStorage().add(calendar)))
}

val DELETE_CALENDAR: Reducer<DeleteCalendar, State> = { state, action ->
    state.getCalendarStorage().require(action.id)
    validateCanDelete(state.canDelete(action.id), action.id)

    noFollowUps(state.updateStorage(state.getCalendarStorage().remove(action.id)))
}

val UPDATE_CALENDAR: Reducer<UpdateCalendar, State> = { state, action ->
    val calendar = action.calendar

    state.getCalendarStorage().require(calendar.id)
    validateCalendar(state, calendar)

    noFollowUps(state.updateStorage(state.getCalendarStorage().update(calendar)))
}

fun validateCalendar(
    state: State,
    calendar: Calendar,
) {
    checkDays(calendar)
    checkMonths(calendar)
    checkEras(state, calendar)
    checkOrigin(state, calendar)
    checkHolidays(state, calendar)
}

private fun checkDays(
    calendar: Calendar,
) {
    when (val days = calendar.days) {
        DayOfTheMonth -> doNothing()
        is Weekdays -> {
            require(days.weekDays.size > 1) { "Requires at least 2 weekdays" }
            require(days.weekDays.map { it.name }.toSet().size == days.weekDays.size) {
                "The names of the weekdays need to be unique!"
            }
        }
    }
}

private fun checkMonths(
    calendar: Calendar,
) {
    require(calendar.months.getSize() > 1) { "Requires at least 2 months" }
    calendar.months.months().forEach { require(it.days > 1) { "Requires at least 2 days per month" } }
    require(calendar.months.months().map { it.name }.toSet().size == calendar.months.getSize()) {
        "The names of the months need to be unique!"
    }
}

private fun checkEras(
    state: State,
    calendar: Calendar,
) {
    if (state.getDefaultCalendarId() == calendar.id) {
        require(calendar.eras.first.startDay.day == 0) { "Default Calendar must not have an offset!" }
    }
}

private fun checkOrigin(
    state: State,
    calendar: Calendar,
) {
    when (val origin = calendar.origin) {
        is ImprovedCalendar -> {
            require(state.getCalendarStorage().contains(origin.parent)) { "Parent calendar must exist!" }
            require(origin.parent != calendar.id) { "Calendar cannot be its own parent!" }
        }

        OriginalCalendar -> doNothing()
    }
}

private fun checkHolidays(
    state: State,
    calendar: Calendar,
) {
    state.getHolidays(calendar.id).forEach { checkRelativeDate(calendar, it.relativeDate) }
}
