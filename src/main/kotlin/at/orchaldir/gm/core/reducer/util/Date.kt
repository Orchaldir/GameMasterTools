package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.selector.getDefaultCalendar

fun checkDate(
    state: State,
    date: Date?,
    message: String = "Date is in the future!",
) {
    val calendar = state.getDefaultCalendar()
    require(calendar.isAfterOrEqualOptional(state.time.currentDate, date)) { message }
}