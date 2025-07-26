package at.orchaldir.gm.app.html.time

import at.orchaldir.gm.app.CATASTROPHE
import at.orchaldir.gm.app.GOD
import at.orchaldir.gm.app.PURPOSE
import at.orchaldir.gm.app.TREATY
import at.orchaldir.gm.app.WAR
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.realm.parseCatastropheId
import at.orchaldir.gm.app.html.realm.parseTreatyId
import at.orchaldir.gm.app.html.realm.parseWarId
import at.orchaldir.gm.app.html.religion.parseGodId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.holiday.*
import at.orchaldir.gm.core.model.world.town.TerrainType
import at.orchaldir.gm.core.selector.util.sortCatastrophes
import at.orchaldir.gm.core.selector.util.sortGods
import at.orchaldir.gm.core.selector.util.sortTreaties
import at.orchaldir.gm.core.selector.util.sortWars
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
        Festival -> +"Festival"
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
        is HolidayOfWar -> {
            +"Remembrance of "
            link(call, state, purpose.war)
        }
    }
}

// edit

fun HtmlBlockTag.editHolidayPurpose(
    state: State,
    purpose: HolidayPurpose,
) {
    val catastrophes = state.sortCatastrophes()
    val gods = state.sortGods()
    val treaties = state.sortTreaties()
    val wars = state.sortWars()

    selectValue("Purpose", PURPOSE, HolidayPurposeType.entries, purpose.getType()) { type ->
        when (type) {
            HolidayPurposeType.Anniversary -> false
            HolidayPurposeType.Catastrophe -> catastrophes.isEmpty()
            HolidayPurposeType.Festival -> false
            HolidayPurposeType.God -> gods.isEmpty()
            HolidayPurposeType.War -> wars.isEmpty()
            HolidayPurposeType.Treaty -> treaties.isEmpty()
        }
    }

    when (purpose) {
        Anniversary, Festival -> doNothing()
        is HolidayOfCatastrophe -> selectElement(
            state,
            "Catastrophe",
            CATASTROPHE,
            catastrophes,
            purpose.catastrophe,
        )

        is HolidayOfGod -> selectElement(
            state,
            "God",
            GOD,
            gods,
            purpose.god,
        )

        is HolidayOfTreaty -> selectElement(
            state,
            "Treaty",
            TREATY,
            treaties,
            purpose.treaty,
        )

        is HolidayOfWar -> selectElement(
            state,
            "War",
            WAR,
            wars,
            purpose.war,
        )
    }
}

// parse

fun parseHolidayPurpose(parameters: Parameters) = when (parse(parameters, PURPOSE, HolidayPurposeType.Anniversary)) {
    HolidayPurposeType.Anniversary -> Anniversary
    HolidayPurposeType.Catastrophe -> HolidayOfCatastrophe(parseCatastropheId(parameters, CATASTROPHE))
    HolidayPurposeType.Festival -> Festival
    HolidayPurposeType.God -> HolidayOfGod(parseGodId(parameters, GOD))
    HolidayPurposeType.Treaty -> HolidayOfTreaty(parseTreatyId(parameters, TREATY))
    HolidayPurposeType.War -> HolidayOfWar(parseWarId(parameters, WAR))
}