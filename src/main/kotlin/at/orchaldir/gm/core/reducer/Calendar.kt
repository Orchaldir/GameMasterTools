package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateCalendar
import at.orchaldir.gm.core.action.DeleteCalendar
import at.orchaldir.gm.core.action.UpdateCalendar
import at.orchaldir.gm.core.model.ElementType
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_CALENDAR: Reducer<CreateCalendar, State> = { state, _ ->
    val calendar = Calendar(state.getCalendarStorage().nextId)

    noFollowUps(state.updateStorage(ElementType.CALENDAR, state.getCalendarStorage().add(calendar)))
}

val DELETE_CALENDAR: Reducer<DeleteCalendar, State> = { state, action ->
    state.getCalendarStorage().require(action.id)
    require(state.canDelete(action.id)) { "Calendar ${action.id.value} is used" }

    noFollowUps(state.updateStorage(ElementType.CALENDAR, state.getCalendarStorage().remove(action.id)))
}

val UPDATE_CALENDAR: Reducer<UpdateCalendar, State> = { state, action ->
    val calendar = action.calendar

    state.getCalendarStorage().require(calendar.id)
    checkDays(calendar)
    checkMonths(calendar)
    checkOrigin(state, calendar)

    noFollowUps(state.updateStorage(ElementType.CALENDAR, state.getCalendarStorage().update(calendar)))
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
            require(state.getCalendarStorage().contains(origin.parent)) { "Parent calendar must exist!" }
            require(origin.parent != calendar.id) { "Calendar cannot be its own parent!" }
        }

        OriginalCalendar -> doNothing()
    }
}
