package at.orchaldir.gm.app.html.model.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.END
import at.orchaldir.gm.app.START
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCatastrophe(
    call: ApplicationCall,
    state: State,
    war: Catastrophe,
) {
    val calendar = state.getDefaultCalendar()

    optionalField(call, state, "Start Date", war.startDate)
    optionalField(call, state, "End Date", war.endDate)
    fieldAge("Duration", calendar.getYears(war.getDuration(state)))
    showDataSources(call, state, war.sources)
}

// edit

fun FORM.editCatastrophe(
    state: State,
    war: Catastrophe,
) {
    selectName(war.name)
    selectOptionalDate(state, "Start Date", war.startDate, combine(START, DATE))
    selectOptionalDate(state, "End Date", war.endDate, combine(END, DATE))
    editDataSources(state, war.sources)
}

// parse

fun parseCatastropheId(parameters: Parameters, param: String) = CatastropheId(parseInt(parameters, param))

fun parseCatastrophe(parameters: Parameters, state: State, id: CatastropheId) = Catastrophe(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, combine(START, DATE)),
    parseOptionalDate(parameters, state, combine(END, DATE)),
    parseDataSources(parameters),
)
