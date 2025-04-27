package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.CALENDAR
import at.orchaldir.gm.app.CURRENT
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.model.time.parseCalendarId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.time.getDefaultCalendarId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showData(
    call: ApplicationCall,
    state: State,
) {
    showTime(call, state)
}

fun HtmlBlockTag.showTime(
    call: ApplicationCall,
    state: State,
) {
    h2 { +"Time" }
    fieldLink("Default Calendar", call, state, state.getDefaultCalendar())
    showCurrentDate(call, state)
}

// edit

fun FORM.editData(state: State) {
    editTime(state)
}

fun FORM.editTime(state: State) {
    h2 { +"Time" }
    selectElement(
        state,
        "Default Calendar",
        CALENDAR,
        state.getCalendarStorage().getAll(),
        state.getDefaultCalendarId(),
    )
    selectDate(state, "Current Date", state.getCurrentDate(), CURRENT)
}

// parse

fun parseData(
    parameters: Parameters,
    default: Calendar,
) = Data(
    parseTime(parameters, default),
)

fun parseTime(
    parameters: Parameters,
    default: Calendar,
) = Time(
    parseCalendarId(parameters, CALENDAR_TYPE),
    parseDay(parameters, default, CURRENT),
)