package at.orchaldir.gm.app.html.model.time

import at.orchaldir.gm.app.CALENDAR
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseCalendarId
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.model.holiday.Holiday
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.selector.getCultures
import at.orchaldir.gm.core.selector.organization.getOrganizations
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showHoliday(
    call: ApplicationCall,
    state: State,
    holiday: Holiday,
) {
    val calendar = state.getCalendarStorage().getOrThrow(holiday.calendar)

    fieldLink("Calendar", call, state, holiday.calendar)
    showRelativeDate("Relative Date", calendar, holiday.relativeDate)
    fieldHolidayPurpose(call, state, holiday.purpose)

    showList("Cultures", state.getCultures(holiday.id)) { culture ->
        link(call, culture)
    }
    showList("Organizations", state.getOrganizations(holiday.id)) { culture ->
        link(call, culture)
    }
}

// edit

fun FORM.editHoliday(
    state: State,
    holiday: Holiday,
) {
    val calendar = state.getCalendarStorage().getOrThrow(holiday.calendar)

    selectName(holiday.name)
    selectElement(state, "Calendar", CALENDAR, state.getCalendarStorage().getAll(), holiday.calendar, true)
    selectRelativeDate(DATE, holiday.relativeDate, calendar)
    editHolidayPurpose(state, holiday.purpose)
}

// parse

fun parseHolidayId(
    parameters: Parameters,
    param: String,
) = HolidayId(parseInt(parameters, param))

fun parseHolidayId(value: String) = HolidayId(value.toInt())

fun parseHoliday(id: HolidayId, parameters: Parameters): Holiday {
    val name = parameters.getOrFail(NAME)

    return Holiday(
        id,
        name,
        parseCalendarId(parameters, CALENDAR_TYPE),
        parseRelativeDate(parameters, DATE),
        parseHolidayPurpose(parameters),
    )
}
