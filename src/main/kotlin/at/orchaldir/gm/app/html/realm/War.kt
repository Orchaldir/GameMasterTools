package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.START
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.selector.realm.getBattles
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.getHolidays
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showWar(
    call: ApplicationCall,
    state: State,
    war: War,
) {
    val calendar = state.getDefaultCalendar()

    fieldAge("Duration", calendar.getYears(war.getDuration(state)))
    optionalField(call, state, "Start Date", war.startDate)
    showWarStatus(call, state, war)
    showWarSides(war)
    showWarParticipants(call, state, war)
    fieldElements(call, state, state.getBattles(war.id))
    fieldElements(call, state, state.getHolidays(war.id))
    showDestroyed(call, state, war.id)
    showDataSources(call, state, war.sources)
}

// edit

fun HtmlBlockTag.editWar(
    call: ApplicationCall,
    state: State,
    war: War,
) {
    selectName(war.name)
    selectOptionalDate(state, "Start Date", war.startDate, combine(START, DATE))
    editWarStatus(state, war.startDate, war)
    editWarSides(war)
    editWarParticipants(state, war)
    editDataSources(state, war.sources)
}

// parse

fun parseWarId(parameters: Parameters, param: String) = WarId(parseInt(parameters, param))
fun parseOptionalWarId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { WarId(it) }

fun parseWar(
    state: State,
    parameters: Parameters,
    id: WarId,
): War {
    val startDate = parseOptionalDate(parameters, state, combine(START, DATE))

    return War(
        id,
        parseName(parameters),
        startDate,
        parseWarStatus(parameters, state),
        parseWarSides(parameters),
        parseWarParticipants(parameters, state, startDate),
        parseDataSources(parameters),
    )
}
