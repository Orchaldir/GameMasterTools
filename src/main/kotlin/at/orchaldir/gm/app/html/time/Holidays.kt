package at.orchaldir.gm.app.html.time

import at.orchaldir.gm.app.HOLIDAY
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.selectElements
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.selector.util.sortHolidays
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showHolidays(
    call: ApplicationCall,
    state: State,
    holidayIds: Set<HolidayId>,
) {
    val holidays = state.sortHolidays(state.getHolidayStorage().get(holidayIds))

    fieldList(call, state, holidays)
}

// edit

fun HtmlBlockTag.editHolidays(
    state: State,
    holidayIds: Set<HolidayId>,
) {
    showDetails("Holidays") {
        val holidays = state.sortHolidays()
        selectElements(state, HOLIDAY, holidays, holidayIds)
    }
}

// parse

fun parseHolidays(parameters: Parameters) = parseElements(parameters, HOLIDAY) { parseHolidayId(it) }