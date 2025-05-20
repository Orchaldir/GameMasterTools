package at.orchaldir.gm.app.html.time

import at.orchaldir.gm.app.CALENDAR
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.action
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.util.field
import at.orchaldir.gm.app.html.util.parseDay
import at.orchaldir.gm.app.html.util.selectDate
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.routes.time.TimeRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.calendar.Calendar
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showTime(
    call: ApplicationCall,
    state: State,
    time: Time,
) {
    val eventsLink = call.application.href(TimeRoutes.ShowEvents())

    h2 { +"Time" }

    fieldLink("Default Calendar", call, state, time.defaultCalendar)
    field(call, state, "Current Date", time.currentDate)
    action(eventsLink, "Events")
}

// edit

fun FORM.editTime(
    state: State,
    time: Time,
) {
    h2 { +"Time" }

    selectElement(
        state,
        "Default Calendar",
        CALENDAR,
        state.getCalendarStorage().getAll(),
        time.defaultCalendar,
    )
    selectDate(state, "Current Date", time.currentDate, DATE)
}

// parse

fun parseTime(
    parameters: Parameters,
    default: Calendar,
) = Time(
    parseCalendarId(parameters, CALENDAR),
    parseDay(parameters, default, DATE),
)