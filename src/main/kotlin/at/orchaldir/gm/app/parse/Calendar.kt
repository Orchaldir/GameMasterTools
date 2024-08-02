package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import io.ktor.http.*

fun parseCalendar(
    parameters: Parameters,
    id: CalendarId,
) = Calendar(id)