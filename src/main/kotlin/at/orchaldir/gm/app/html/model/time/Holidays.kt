package at.orchaldir.gm.app.html.model.time

import at.orchaldir.gm.app.HOLIDAY
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.selectElements
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.showList
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.selector.util.sortHolidays
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showHolidays(
    call: ApplicationCall,
    state: State,
    holidayIds: Set<HolidayId>,
) {
    val holidays = state.sortHolidays(state.getHolidayStorage().get(holidayIds))

    showDetails("Holidays") {
        showList(holidays) { holiday ->
            link(call, state, holiday)
        }
    }
}

// edit

fun FORM.editHolidays(
    state: State,
    culture: Culture,
) {
    showDetails("Holidays") {
        val cultures = state.sortHolidays()
        selectElements(state, HOLIDAY, cultures, culture.holidays)
    }
}

// parse

fun parseHolidays(parameters: Parameters) = parseElements(parameters, HOLIDAY) { parseHolidayId(it) }