package at.orchaldir.gm.app.html.model.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.END
import at.orchaldir.gm.app.START
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.selector.character.getCharactersKilledInCatastrophe
import at.orchaldir.gm.core.selector.getHolidays
import at.orchaldir.gm.core.selector.realm.getRealmsDestroyedByCatastrophe
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCatastrophe(
    call: ApplicationCall,
    state: State,
    catastrophe: Catastrophe,
) {
    val calendar = state.getDefaultCalendar()

    optionalField(call, state, "Start Date", catastrophe.startDate)
    optionalField(call, state, "End Date", catastrophe.endDate)
    fieldAge("Duration", calendar.getYears(catastrophe.getDuration(state)))
    showCauseOfCatastrophe(call, state, catastrophe.cause)
    fieldList(call, state, "Killed Characters", state.getCharactersKilledInCatastrophe(catastrophe.id))
    fieldList(call, state, "Destroyed Realms", state.getRealmsDestroyedByCatastrophe(catastrophe.id))
    fieldList(call, state, state.getHolidays(catastrophe.id))
    showDataSources(call, state, catastrophe.sources)
}

// edit

fun FORM.editCatastrophe(
    state: State,
    catastrophe: Catastrophe,
) {
    selectName(catastrophe.name)
    selectOptionalDate(state, "Start Date", catastrophe.startDate, combine(START, DATE))
    selectOptionalDate(state, "End Date", catastrophe.endDate, combine(END, DATE))
    editCauseOfCatastrophe(state, catastrophe)
    editDataSources(state, catastrophe.sources)
}

// parse

fun parseCatastropheId(parameters: Parameters, param: String) = CatastropheId(parseInt(parameters, param))
fun parseOptionalCatastropheId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { CatastropheId(it) }

fun parseCatastrophe(parameters: Parameters, state: State, id: CatastropheId) = Catastrophe(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, combine(START, DATE)),
    parseOptionalDate(parameters, state, combine(END, DATE)),
    parseCauseOfCatastrophe(parameters),
    parseDataSources(parameters),
)
