package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.date.Date
import at.orchaldir.gm.core.model.calendar.date.Day
import at.orchaldir.gm.core.model.calendar.date.Year
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.field(label: String, calendar: Calendar, date: Date) {
    field(label) {
        when (date) {
            is Day -> TODO()
            is Year -> TODO()
        }
    }
}

