package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.CALENDAR
import at.orchaldir.gm.app.CURRENCY
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.action
import at.orchaldir.gm.app.html.fieldLink
import at.orchaldir.gm.app.html.model.economy.parseCurrencyId
import at.orchaldir.gm.app.html.model.time.parseCalendarId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.routes.time.TimeRoutes
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.selector.getDefaultCurrencyId
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.time.getDefaultCalendarId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showData(
    call: ApplicationCall,
    state: State,
) {
    fieldLink("Default Currency", call, state, state.getDefaultCurrencyId())
    showTime(call, state)
}

fun HtmlBlockTag.showTime(
    call: ApplicationCall,
    state: State,
) {
    val eventsLink = call.application.href(TimeRoutes.ShowEvents())

    h2 { +"Time" }
    fieldLink("Default Calendar", call, state, state.getDefaultCalendar())
    showCurrentDate(call, state)
    action(eventsLink, "Events")
}

// edit

fun FORM.editData(state: State) {
    selectElement(
        state,
        "Default Currency",
        CURRENCY,
        state.getCurrencyStorage().getAll(),
        state.getDefaultCurrencyId(),
    )
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
    selectDate(state, "Current Date", state.getCurrentDate(), DATE)
}

// parse

fun parseData(
    parameters: Parameters,
    default: Calendar,
) = Data(
    parseCurrencyId(parameters, CURRENCY),
    emptyList(),
    parseTime(parameters, default),
)

fun parseTime(
    parameters: Parameters,
    default: Calendar,
) = Time(
    parseCalendarId(parameters, CALENDAR),
    parseDay(parameters, default, DATE),
)