package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.HISTORY
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.History
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun <T> HtmlBlockTag.showHistory(
    call: ApplicationCall,
    state: State,
    history: History<T>,
    label: String,
    showEntry: HtmlBlockTag.(ApplicationCall, State, T) -> Unit,
) {
    showList("Previous $label", history.previousEntries) { previous ->
        +"Until "
        showDate(call, state, previous.until)
        +": "
        showEntry(call, state, previous.entry)
    }
    field(label) {
        showEntry(call, state, history.current)
    }
}

fun <T> FORM.selectHistory(
    state: State,
    param: String,
    ownership: History<T>,
    startDate: Date,
    label: String,
    selectEntry: HtmlBlockTag.(State, String, T, Date) -> Unit,
) {
    val previousOwnersParam = combine(param, HISTORY)
    selectInt("Previous $label", ownership.previousEntries.size, 0, 100, 1, previousOwnersParam, true)
    var minDate = startDate.next()

    showListWithIndex(ownership.previousEntries) { index, previous ->
        val previousParam = combine(previousOwnersParam, index)
        selectEntry(state, previousParam, previous.entry, minDate)
        selectDate(state, "Until", previous.until, combine(previousParam, DATE), minDate)

        minDate = previous.until.next()
    }

    selectEntry(state, param, ownership.current, minDate)
}
