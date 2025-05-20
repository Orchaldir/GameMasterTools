package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.html.economy.editEconomy
import at.orchaldir.gm.app.html.economy.parseEconomy
import at.orchaldir.gm.app.html.economy.showEconomy
import at.orchaldir.gm.app.html.time.editTime
import at.orchaldir.gm.app.html.time.parseTime
import at.orchaldir.gm.app.html.time.showTime
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showData(
    call: ApplicationCall,
    state: State,
) {
    showEconomy(call, state, state.data.economy)
    showTime(call, state, state.data.time)
}


// edit

fun FORM.editData(state: State, data: Data) {
    editEconomy(state, data.economy)
    editTime(state, data.time)
}

// parse

fun parseData(
    parameters: Parameters,
    default: Calendar,
) = Data(
    parseEconomy(parameters),
    parseTime(parameters, default),
)
