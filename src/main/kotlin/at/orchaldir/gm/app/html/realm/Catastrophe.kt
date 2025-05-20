package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.END
import at.orchaldir.gm.app.START
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.getHolidays
import at.orchaldir.gm.core.selector.world.getRegionsCreatedBy
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
    showDestroyed(call, state, catastrophe.id)
    fieldList(call, state, "Created Regions", state.getRegionsCreatedBy(catastrophe.id))
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
