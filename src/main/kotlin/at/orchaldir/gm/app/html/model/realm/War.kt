package at.orchaldir.gm.app.html.model.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.END
import at.orchaldir.gm.app.REALM
import at.orchaldir.gm.app.START
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.selector.character.getCharactersKilledInWar
import at.orchaldir.gm.core.selector.realm.getBattles
import at.orchaldir.gm.core.selector.realm.getRealmsDestroyedByWar
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.sortBattles
import at.orchaldir.gm.core.selector.util.sortRealms
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
    optionalField(call, state, "End Date", war.endDate)
    fieldAge("Duration", calendar.getYears(war.getDuration(state)))
    fieldList(call, state, battles)
    fieldIdList(call, state, "Participating Realms", war.realms)
    fieldList(call, state, "Destroyed Realms", state.getRealmsDestroyedByWar(war.id))
    fieldList(call, state, "Killed Characters", state.getCharactersKilledInWar(war.id))
    showDataSources(call, state, war.sources)
}

// edit

fun FORM.editWar(
    state: State,
    war: War,
) {
    selectName(war.name)
    selectOptionalDate(state, "Start Date", war.startDate, combine(START, DATE))
    selectOptionalDate(state, "End Date", war.endDate, combine(END, DATE))
    selectElements(state, "Realms", REALM, state.sortRealms(), war.realms)
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
    parseOptionalDate(parameters, state, combine(END, DATE)),
    parseElements(parameters, REALM, ::parseRealmId),
    parseDataSources(parameters),
)
