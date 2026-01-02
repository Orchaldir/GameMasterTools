package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.HISTORY
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun <T> HtmlBlockTag.showHistory(
    call: ApplicationCall,
    state: State,
    history: History<T?>,
    label: String,
    nullLabel: String,
    showEntry: HtmlBlockTag.(ApplicationCall, State, T) -> Unit,
) {
    showHistory(call, state, history, label) { _, _, entry ->
        if (entry != null) {
            showEntry(call, state, entry)
        } else {
            +nullLabel
        }
    }
}

fun <T> HtmlBlockTag.showHistory(
    call: ApplicationCall,
    state: State,
    history: History<T>,
    label: String,
    showEntry: HtmlBlockTag.(ApplicationCall, State, T) -> Unit,
) {
    showDetails(label, true) {
        fieldList("Previously", history.previousEntries) { previous ->
            +"Until "
            showDate(call, state, previous.until)
            +": "
            showEntry(call, state, previous.entry)
        }
        field("Currently") {
            showEntry(call, state, history.current)
        }
    }
}

// edit

fun <T> HtmlBlockTag.selectHistory(
    state: State,
    param: String,
    history: History<T>,
    label: String,
    startDate: Date? = null,
    endDate: Date? = null,
    selectEntry: HtmlBlockTag.(State, String, T, Date?) -> Unit,
) {
    val previousOwnersParam = combine(param, HISTORY)
    var minDate = startDate?.next()

    showDetails(label, true) {
        selectInt("Previously", history.previousEntries.size, 0, 100, 1, previousOwnersParam)

        showListWithIndex(history.previousEntries) { index, previous ->
            val previousParam = combine(previousOwnersParam, index)
            selectEntry(state, previousParam, previous.entry, minDate)
            selectDate(state, "Until", previous.until, combine(previousParam, DATE), minDate, endDate)

            minDate = previous.until.next()
        }

        selectEntry(state, param, history.current, minDate)
    }
}

// parse

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
