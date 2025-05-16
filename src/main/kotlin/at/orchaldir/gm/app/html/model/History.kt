package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.HISTORY
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

fun <T> HtmlBlockTag.showHistory(
    call: ApplicationCall,
    state: State,
    history: History<T>,
    label: String,
    showEntry: HtmlBlockTag.(ApplicationCall, State, T) -> Unit,
) {
    fieldList("Previous $label", history.previousEntries) { previous ->
        +"Until "
        showDate(call, state, previous.until)
        +": "
        showEntry(call, state, previous.entry)
    }
    field(label) {
        showEntry(call, state, history.current)
    }
}

fun <T> HtmlBlockTag.selectHistory(
    state: State,
    param: String,
    history: History<T>,
    startDate: Date?,
    label: String,
    selectEntry: HtmlBlockTag.(State, String, T, Date?) -> Unit,
) {
    val previousOwnersParam = combine(param, HISTORY)
    selectInt("Previous $label Entries", history.previousEntries.size, 0, 100, 1, previousOwnersParam)
    var minDate = startDate?.next()

    showListWithIndex(history.previousEntries) { index, previous ->
        val previousParam = combine(previousOwnersParam, index)
        selectEntry(state, previousParam, previous.entry, minDate)
        selectDate(state, "Until", previous.until, combine(previousParam, DATE), minDate)

        minDate = previous.until.next()
    }

    selectEntry(state, param, history.current, minDate)
}

fun <T> parseHistory(
    parameters: Parameters,
    param: String,
    state: State,
    startDate: Date?,
    parseEntry: (Parameters, State, String) -> T,
) = History(
    parseEntry(parameters, state, param),
    parseHistoryEntries(parameters, param, state, startDate, parseEntry),
)

private fun <T> parseHistoryEntries(
    parameters: Parameters,
    param: String,
    state: State,
    startDate: Date?,
    parseEntry: (Parameters, State, String) -> T,
): List<HistoryEntry<T>> {
    val historyParam = combine(param, HISTORY)
    val count = parseInt(parameters, historyParam, 0)
    var minDate = startDate?.next()

    return (0..<count)
        .map {
            val previousOwner = parseHistoryEntry(parameters, state, combine(historyParam, it), minDate, parseEntry)
            minDate = previousOwner.until.next()

            previousOwner
        }
}

private fun <T> parseHistoryEntry(
    parameters: Parameters,
    state: State,
    param: String,
    minDate: Date?,
    parseEntry: (Parameters, State, String) -> T,
) = HistoryEntry(
    parseEntry(parameters, state, param),
    parseDate(parameters, state, combine(param, DATE), minDate),
)
