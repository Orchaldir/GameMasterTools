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

fun <ID, ELEMENT> State.requireExists(storage: Storage<ID, ELEMENT>, id: ID, date: Date?): ELEMENT
        where ID : Id<ID>,
              ELEMENT : Element<ID>,
              ELEMENT : HasStartDate =
    requireExists(storage, id, date) {
        "The ${id.type()} ${id.value()} doesn't exist at the required date!"
    }

fun <ID, ELEMENT> State.requireExists(
    storage: Storage<ID, ELEMENT>,
    id: ID,
    date: Date?,
    message: (ID) -> String,
): ELEMENT where ID : Id<ID>,
                 ELEMENT : Element<ID>,
                 ELEMENT : HasStartDate {
    val element = storage.getOrThrow(id) {
        message(id)
    }

    require(exists(element, date)) {
        message(element.id())
    }

    return element
}