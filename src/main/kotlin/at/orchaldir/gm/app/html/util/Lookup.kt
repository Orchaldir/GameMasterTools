package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.HISTORY
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Lookup
import at.orchaldir.gm.core.model.util.LookupEntry
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun <T> HtmlBlockTag.showLookup(
    call: ApplicationCall,
    state: State,
    lookup: Lookup<T>,
    label: String,
    showValue: HtmlBlockTag.(T) -> Unit,
) {
    showDetails(label, true) {
        fieldList("Previously", lookup.previousEntries) { previous ->
            +"Until ${previous.until}: "
            showValue(previous.value)
        }
        field("Currently") {
            showValue(lookup.current)
        }
    }
}

// edit

fun <T> HtmlBlockTag.selectLookup(
    state: State,
    param: String,
    history: Lookup<T>,
    label: String,
    start: Int,
    end: Int,
    selectValue: HtmlBlockTag.(State, String, T) -> Unit,
) {
    val previousOwnersParam = combine(param, HISTORY)
    var minUntil = start + 1

    showDetails(label, true) {
        selectInt("Previously", history.previousEntries.size, 0, 100, 1, previousOwnersParam)

        showListWithIndex(history.previousEntries) { index, previous ->
            val previousParam = combine(previousOwnersParam, index)
            selectValue(state, previousParam, previous.value)
            selectInt(
                "Until",
                previous.until,
                minUntil,
                end,
                1,
                combine(previousParam, DATE),
            )

            minUntil = previous.until + 1
        }

        selectValue(state, param, history.current)
    }
}

// parse

fun <T> parseLookup(
    parameters: Parameters,
    param: String,
    state: State,
    start: Int,
    parseValue: (Parameters, State, String) -> T,
) = Lookup(
    parseValue(parameters, state, param),
    parseLookupEntries(parameters, param, state, start, parseValue),
)

private fun <T> parseLookupEntries(
    parameters: Parameters,
    param: String,
    state: State,
    start: Int,
    parseValue: (Parameters, State, String) -> T,
): List<LookupEntry<T>> {
    val historyParam = combine(param, HISTORY)
    val count = parseInt(parameters, historyParam, 0)
    var minDate = start +1

    return (0..<count)
        .map {
            val previousOwner = parseLookupEntry(parameters, state, combine(historyParam, it), minDate, parseValue)
            minDate = previousOwner.until + 1

            previousOwner
        }
}

private fun <T> parseLookupEntry(
    parameters: Parameters,
    state: State,
    param: String,
    minDate: Int,
    parseValue: (Parameters, State, String) -> T,
) = LookupEntry(
    parseValue(parameters, state, param),
    parseInt(parameters, combine(param, DATE), minDate),
)
