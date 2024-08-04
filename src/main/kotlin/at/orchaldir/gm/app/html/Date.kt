package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.calendar.date.*
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.field(state: State, label: String, date: Date) {
    field(label, state.calendars.getOrThrow(CalendarId(0)), date)
}

fun HtmlBlockTag.field(label: String, calendar: Calendar, date: Date) {
    val calendarDate = calendar.resolve(date)

    field(label) {
        +calendar.eras.resolve(calendarDate)
    }
}

