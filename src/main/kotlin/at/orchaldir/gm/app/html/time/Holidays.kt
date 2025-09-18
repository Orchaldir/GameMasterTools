package at.orchaldir.gm.app.html.time

import at.orchaldir.gm.app.HOLIDAY
import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.app.html.selectElements
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showHolidays(
    call: ApplicationCall,
    state: State,
    holidayIds: Set<HolidayId>,
) {
    fieldElements(call, state, state.getHolidayStorage().get(holidayIds))
}

// edit

fun HtmlBlockTag.editHolidays(
    state: State,
    holidayIds: Set<HolidayId>,
) {
    showDetails("Holidays") {
        selectElements(state, HOLIDAY, state.getHolidayStorage().getAll(), holidayIds)
    }
}

// parse

fun parseHolidays(parameters: Parameters) = parseElements(parameters, HOLIDAY) { parseHolidayId(it) }