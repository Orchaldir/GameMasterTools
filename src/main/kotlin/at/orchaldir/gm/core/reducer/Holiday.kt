package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateHoliday
import at.orchaldir.gm.core.action.DeleteHoliday
import at.orchaldir.gm.core.action.UpdateHoliday
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.holiday.*
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.DayOfTheMonth
import at.orchaldir.gm.core.model.time.calendar.MonthDefinition
import at.orchaldir.gm.core.model.time.calendar.Weekdays
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_HOLIDAY: Reducer<CreateHoliday, State> = { state, _ ->
    val holiday = Holiday(state.getHolidayStorage().nextId)

    noFollowUps(state.updateStorage(state.getHolidayStorage().add(holiday)))
}

val DELETE_HOLIDAY: Reducer<DeleteHoliday, State> = { state, action ->
    state.getHolidayStorage().require(action.id)
    validateCanDelete(state.canDelete(action.id), action.id)

    noFollowUps(state.updateStorage(state.getHolidayStorage().remove(action.id)))
}

val UPDATE_HOLIDAY: Reducer<UpdateHoliday, State> = { state, action ->
    val holiday = action.holiday

    state.getHolidayStorage().require(holiday.id)
    validateHoliday(state, holiday)

    noFollowUps(state.updateStorage(state.getHolidayStorage().update(holiday)))
}

fun validateHoliday(state: State, holiday: Holiday) {
    val calendar = state.getCalendarStorage().getOrThrow(holiday.calendar)

    checkPurpose(state, holiday.purpose)
    checkRelativeDate(calendar, holiday.relativeDate)
}

fun checkPurpose(state: State, purpose: HolidayPurpose) {
    when (purpose) {
        Anniversary -> doNothing()
        is HolidayOfCatastrophe -> state.getCatastropheStorage().require(purpose.catastrophe)
        is HolidayOfGod -> state.getGodStorage().require(purpose.god)
        is HolidayOfTreaty -> state.getTreatyStorage().require(purpose.treaty)
    }
}

fun checkRelativeDate(calendar: Calendar, relativeDate: RelativeDate) {
    when (relativeDate) {
        is DayInMonth -> {
            require(relativeDate.dayIndex < calendar.getMinDaysPerMonth()) { "Holiday is outside at least one month!" }
        }

        is DayInYear -> {
            val month = checkMonth(calendar, relativeDate.monthIndex)
            require(relativeDate.dayIndex < month.days) { "Holiday is outside the month ${month.name.text}!" }
        }

        is WeekdayInMonth -> {
            checkMonth(calendar, relativeDate.monthIndex)
            when (calendar.days) {
                DayOfTheMonth -> error("A holiday on a weekday requires a calendar with weekdays!")
                is Weekdays -> require(relativeDate.weekdayIndex < calendar.days.weekDays.size) { "Holiday is on an unknown weekday!" }
            }
        }
    }
}

private fun checkMonth(
    calendar: Calendar,
    monthIndex: Int,
): MonthDefinition {
    require(monthIndex < calendar.months.getSize()) { "Holiday is in an unknown month!" }
    return calendar.months.getMonth(monthIndex)
}
