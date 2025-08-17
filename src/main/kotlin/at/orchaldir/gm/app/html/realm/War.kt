package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.START
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.selector.realm.getBattles
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.getHolidays
import at.orchaldir.gm.core.selector.util.sortBattles
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showWar(
    call: ApplicationCall,
    state: State,
    war: War,
) {
    val battles = state.sortBattles(state.getBattles(war.id))
    val calendar = state.getDefaultCalendar()

    optionalField(call, state, "Start Date", war.startDate)
    showWarStatus(call, state, war.status)
    fieldAge("Duration", calendar.getYears(war.getDuration(state)))
    showWarSides(war)
    fieldList(call, state, battles)
    //fieldIdList(call, state, "Participating Realms", war.realms)
    showDestroyed(call, state, war.id)
    fieldList(call, state, state.getHolidays(war.id))
    showDataSources(call, state, war.sources)
}

// edit

fun FORM.editWar(
    state: State,
    war: War,
) {
    selectName(war.name)
    selectOptionalDate(state, "Start Date", war.startDate, combine(START, DATE))
    editWarStatus(state, war.startDate, war.status)
    editWarSides(war)
    //selectElements(state, "Realms", REALM, state.sortRealms(), war.realms)
    editDataSources(state, war.sources)
}

// parse

fun parseWarId(parameters: Parameters, param: String) = WarId(parseInt(parameters, param))
fun parseOptionalWarId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { WarId(it) }

fun parseWar(parameters: Parameters, state: State, id: WarId) = War(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, combine(START, DATE)),
    parseWarStatus(parameters, state),
    parseWarSides(parameters),
    emptyList(),
    //parseElements(parameters, REALM, ::parseRealmId),
    parseDataSources(parameters),
)
