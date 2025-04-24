package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun <ELEMENT : HasStartDate> State.exists(element: ELEMENT, date: Date?) = getDefaultCalendar()
    .isAfterOrEqualOptional(date, element.startDate())

fun <ID, ELEMENT> State.getExistingElements(storage: Storage<ID, ELEMENT>, date: Date?)
        where ID : Id<ID>,
              ELEMENT : Element<ID>,
              ELEMENT : HasStartDate =
    getExistingElements(storage.getAll(), date)

fun <ELEMENT : HasStartDate> State.getExistingElements(elements: Collection<ELEMENT>, date: Date?) = if (date == null) {
    elements
} else {
    elements.filter { exists(it, date) }
}