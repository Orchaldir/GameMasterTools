package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.getCurrentDate

fun checkDate(
    state: State,
    date: Date?,
    noun: String,
) {
    if (date != null) {
        val calendar = state.getDefaultCalendar()
        require(calendar.isAfterOrEqual(state.getCurrentDate(), date)) { "Date ($noun) is in the future!" }
    }
}