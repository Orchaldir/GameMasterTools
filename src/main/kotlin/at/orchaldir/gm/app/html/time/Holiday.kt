package at.orchaldir.gm.app.html.time

import at.orchaldir.gm.app.CALENDAR
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.holiday.Holiday
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.model.time.calendar.CALENDAR_TYPE
import at.orchaldir.gm.core.selector.culture.getCultures
import at.orchaldir.gm.core.selector.organization.getOrganizations
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showHoliday(
    call: ApplicationCall,
    state: State,
    holiday: Holiday,
) {
    val calendar = state.getCalendarStorage().getOrThrow(holiday.calendar)

    fieldLink("Calendar", call, state, holiday.calendar)
    showRelativeDate("Relative Date", calendar, holiday.relativeDate)
    fieldHolidayPurpose(call, state, holiday.purpose)

    fieldList(call, state, state.getCultures(holiday.id))
    fieldList(call, state, state.getOrganizations(holiday.id))
}

// edit

fun FORM.editHoliday(
    state: State,
    holiday: Holiday,
) {
    val calendar = state.getCalendarStorage().getOrThrow(holiday.calendar)

    selectName(holiday.name)
    selectElement(state, "Calendar", CALENDAR, state.getCalendarStorage().getAll(), holiday.calendar)
    selectRelativeDate(DATE, holiday.relativeDate, calendar)
    editHolidayPurpose(state, holiday.purpose)
}

// parse

fun parseHolidayId(
    parameters: Parameters,
    param: String,
) = HolidayId(parseInt(parameters, param))

fun parseHolidayId(value: String) = HolidayId(value.toInt())

fun parseHoliday(id: HolidayId, parameters: Parameters) = Holiday(
    id,
    parseName(parameters),
    parseCalendarId(parameters, CALENDAR_TYPE),
    parseRelativeDate(parameters, DATE),
    parseHolidayPurpose(parameters),
)
