package at.orchaldir.gm.app.html.model.time

import at.orchaldir.gm.app.CATASTROPHE
import at.orchaldir.gm.app.GOD
import at.orchaldir.gm.app.PURPOSE
import at.orchaldir.gm.app.TREATY
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.realm.parseCatastropheId
import at.orchaldir.gm.app.html.model.realm.parseTreatyId
import at.orchaldir.gm.app.html.model.religion.parseGodId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.holiday.Anniversary
import at.orchaldir.gm.core.model.holiday.HolidayOfCatastrophe
import at.orchaldir.gm.core.model.holiday.HolidayOfGod
import at.orchaldir.gm.core.model.holiday.HolidayOfTreaty
import at.orchaldir.gm.core.model.holiday.HolidayPurpose
import at.orchaldir.gm.core.model.holiday.HolidayPurposeType
import at.orchaldir.gm.core.selector.util.sortCatastrophes
import at.orchaldir.gm.core.selector.util.sortGods
import at.orchaldir.gm.core.selector.util.sortTreaties
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
        is HolidayOfCatastrophe -> {
            +"Remembrance of "
            link(call, state, purpose.catastrophe)
        }
        is HolidayOfGod -> {
            +"Worship of "
            link(call, state, purpose.god)
        }
        is HolidayOfTreaty -> {
            +"Celebration of "
            link(call, state, purpose.treaty)
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
        is HolidayOfCatastrophe -> selectElement(
            state,
            "Catastrophe",
            CATASTROPHE,
            state.sortCatastrophes(),
            purpose.catastrophe,
        )

        is HolidayOfGod -> selectElement(
            state,
            "God",
            GOD,
            state.sortGods(),
            purpose.god,
        )

        is HolidayOfTreaty -> selectElement(
            state,
            "Treaty",
            TREATY,
            state.sortTreaties(),
            purpose.treaty,
        )
    }
}

// parse

fun parseHolidayPurpose(parameters: Parameters) = when (parse(parameters, PURPOSE, HolidayPurposeType.Anniversary)) {
    HolidayPurposeType.Anniversary -> Anniversary
    HolidayPurposeType.Catastrophe -> HolidayOfCatastrophe(parseCatastropheId(parameters, CATASTROPHE))
    HolidayPurposeType.God -> HolidayOfGod(parseGodId(parameters, GOD))
    HolidayPurposeType.Treaty -> HolidayOfTreaty(parseTreatyId(parameters, TREATY))
}