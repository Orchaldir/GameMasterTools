package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.selector.time.getDefaultCalendar

fun checkDate(
    state: State,
    date: Date?,
    noun: String,
) {
    if (date != null) {
        val calendar = state.getDefaultCalendar()
        require(calendar.isAfterOrEqual(state.time.currentDate, date)) { "Date ($noun) is in the future!" }
    }
}