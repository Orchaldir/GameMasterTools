package at.orchaldir.gm.app.html.model.time

import at.orchaldir.gm.app.CALENDAR
import at.orchaldir.gm.app.CURRENT
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.model.parseDay
import at.orchaldir.gm.app.html.model.selectDate
import at.orchaldir.gm.app.html.model.showCurrentDate
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.time.calendar.Calendar
import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTime(
    call: ApplicationCall,
    state: State,
) {
    fieldLink("Default Calendar", call, state, state.time.defaultCalendar)
    showCurrentDate(call, state)
}

// edit

fun FORM.editTime(state: State) {
    selectElement(
        state,
        "Default Calendar",
        CALENDAR,
        state.getCalendarStorage().getAll(),
        state.time.defaultCalendar,
    )
    selectDate(state, "Current Date", state.time.currentDate, CURRENT)
}

// parse

fun parseTime(
    parameters: Parameters,
    default: Calendar,
) = Time(
    parseCalendarId(parameters, CALENDAR_TYPE),
    parseDay(parameters, default, CURRENT),
)