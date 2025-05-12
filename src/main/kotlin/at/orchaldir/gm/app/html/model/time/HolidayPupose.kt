package at.orchaldir.gm.app.html.model.time

import at.orchaldir.gm.app.GOD
import at.orchaldir.gm.app.PURPOSE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.religion.parseGodId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.holiday.Anniversary
import at.orchaldir.gm.core.model.holiday.HolidayOfGod
import at.orchaldir.gm.core.model.holiday.HolidayPurpose
import at.orchaldir.gm.core.model.holiday.HolidayPurposeType
import at.orchaldir.gm.core.selector.util.sortGods
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldHolidayPurpose(
    call: ApplicationCall,
    state: State,
    purpose: HolidayPurpose,
) {
    field("Purpose") {
        displayHolidayPurpose(call, state, purpose)
    }
}

fun HtmlBlockTag.displayHolidayPurpose(
    call: ApplicationCall,
    state: State,
    purpose: HolidayPurpose,
) {
    when (purpose) {
        Anniversary -> +"Anniversary"
        is HolidayOfGod -> {
            +"Worship of "
            link(call, state, purpose.god)
        }
    }
}

// edit

fun HtmlBlockTag.editHolidayPurpose(
    state: State,
    purpose: HolidayPurpose,
) {
    selectValue("Purpose", PURPOSE, HolidayPurposeType.entries, purpose.getType())

    when (purpose) {
        Anniversary -> doNothing()
        is HolidayOfGod -> selectElement(state, "God", GOD, state.sortGods(), purpose.god)
    }
}

// parse

fun parseHolidayPurpose(parameters: Parameters) = when (parse(parameters, PURPOSE, HolidayPurposeType.Anniversary)) {
    HolidayPurposeType.Anniversary -> Anniversary
    HolidayPurposeType.God -> HolidayOfGod(parseGodId(parameters, GOD))
}