package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.HasStartAndEndDate
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id

fun validateDate(
    state: State,
    date: Date?,
    noun: String,
) {
    if (date != null) {
        val calendar = state.getDefaultCalendar()
        require(calendar.isAfterOrEqual(state.getCurrentDate(), date)) { "Date ($noun) is in the future!" }
    }
}

fun <ID : Id<ID>, T> validateHasStartAndEnd(state: State, element: T) where
        T : HasStartAndEndDate,
        T : Element<ID> {
    validateDate(state, element.startDate(), "Start Date")
    require(state.getDefaultCalendar().isAfterOrEqualOptional(element.endDate(), element.startDate())) {
        val id = element.id()
        "The ${id.print()} must end after it started!"
    }
}