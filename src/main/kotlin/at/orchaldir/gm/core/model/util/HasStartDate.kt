package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Duration
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.time.date.getEndDay
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.time.getDefaultCalendar

interface HasStartDate {

    fun startDate(): Date?

}

interface HasStartAndEndDate : HasStartDate {

    fun endDate(): Date?

    fun hasSameStartAndEnd() = startDate() != null && startDate() == endDate()

    fun getDuration(state: State): Duration {
        val calendar = state.getDefaultCalendar()
        val startDate = startDate()
        val endDate = endDate()

        return if (startDate != null && endDate != null) {
            calendar.getDuration(startDate, calendar.getEndDay(endDate))
        } else if (startDate != null) {
            calendar.getDuration(startDate, state.getCurrentDate())
        } else {
            Duration(0)
        }
    }

    fun getAgeInYears(state: State) = state
        .getDefaultCalendar()
        .getYears(getDuration(state))
}

interface HasComplexStartDate {

    fun startDate(state: State): Date?

}