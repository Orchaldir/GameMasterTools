package at.orchaldir.gm.app.html.model.time

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.*
import at.orchaldir.gm.core.model.holiday.*
import at.orchaldir.gm.core.selector.getCultures
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

    showList("Cultures", state.getCultures(holiday.id)) { culture ->
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
    )
}
