package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.ImprovedCalendar
import at.orchaldir.gm.core.model.calendar.OriginalCalendar

fun State.canDelete(calendar: CalendarId) = getChildren(calendar).isEmpty() &&
        getCultures(calendar).isEmpty()

fun State.getChildren(calendar: CalendarId) = getCalendarStorage().getAll().filter {
    when (it.origin) {
        is ImprovedCalendar -> it.origin.parent == calendar
        OriginalCalendar -> false
    }
}

fun State.getDefaultCalendar() = getCalendarStorage().getOrThrow(time.defaultCalendar)

fun State.getPossibleParents(calendar: CalendarId) = getCalendarStorage().getAll().filter { it.id != calendar }
