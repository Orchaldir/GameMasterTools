package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.selector.time.getDefaultCalendar

fun <ELEMENT : HasStartDate> State.exists(element: ELEMENT, date: Date?) = getDefaultCalendar()
    .isAfterOrEqualOptional(date, element.startDate())

fun <ELEMENT : HasStartDate> State.getExistingElements(elements: Collection<ELEMENT>, date: Date?) = if (date == null) {
    elements
} else {
    elements.filter { exists(it, date) }
}