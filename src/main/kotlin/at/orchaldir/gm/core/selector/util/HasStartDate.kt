package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.date.getEndDay
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun <ELEMENT : HasStartDate> State.exists(element: ELEMENT, date: Date?) = if (date == null) {
    true
} else {
    val endDay = getDefaultCalendar().getEndDay(date)
    exists(element, endDay)
}

fun <ELEMENT : HasStartDate> State.exists(element: ELEMENT, day: Day) = getDefaultCalendar()
    .isAfterOrEqualOptional(day, element.startDate())

fun <ID, ELEMENT> State.getExistingElements(storage: Storage<ID, ELEMENT>, date: Date?)
        where ID : Id<ID>,
              ELEMENT : Element<ID>,
              ELEMENT : HasStartDate =
    getExistingElements(storage.getAll(), date)

fun <ELEMENT : HasStartDate> State.getExistingElements(elements: Collection<ELEMENT>, date: Date?) = if (date == null) {
    elements
} else {
    val endDay = getDefaultCalendar().getEndDay(date)
    elements.filter { exists(it, endDay) }
}

fun <ID, ELEMENT> State.requireExist(storage: Storage<ID, ELEMENT>, ids: Collection<ID>, date: Date?)
        where ID : Id<ID>,
              ELEMENT : Element<ID>,
              ELEMENT : HasStartDate {
    ids.forEach { id -> requireExists(storage, id, date) }
}

fun <ID, ELEMENT> State.requireExists(id: ID, date: Date?): ELEMENT
        where ID : Id<ID>,
              ELEMENT : Element<ID>,
              ELEMENT : HasStartDate =
    requireExists(getStorage(id), id, date)

fun <ID, ELEMENT> State.requireExists(id: ID, date: Date?, message: () -> String): ELEMENT
        where ID : Id<ID>,
              ELEMENT : Element<ID>,
              ELEMENT : HasStartDate =
    requireExists(getStorage(id), id, date, message)

fun <ID, ELEMENT> State.requireExists(storage: Storage<ID, ELEMENT>, id: ID, date: Date?): ELEMENT
        where ID : Id<ID>,
              ELEMENT : Element<ID>,
              ELEMENT : HasStartDate =
    requireExists(storage, id, date) {
        id.print()
    }

fun <ID, ELEMENT> State.requireExists(
    storage: Storage<ID, ELEMENT>,
    id: ID,
    date: Date?,
    noun: () -> String,
): ELEMENT where ID : Id<ID>,
                 ELEMENT : Element<ID>,
                 ELEMENT : HasStartDate {
    val element = storage.getOrThrow(id) {
        "Requires unknown ${noun()}!"
    }

    require(exists(element, date)) {
        "${noun()} doesn't exist at the required date!"
    }

    return element
}