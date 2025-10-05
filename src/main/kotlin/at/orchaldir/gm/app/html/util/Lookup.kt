package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.CURRENT
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.HISTORY
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.util.Lookup
import at.orchaldir.gm.core.model.util.LookupEntry
import io.ktor.http.*
import kotlinx.html.*

// show

fun <T> HtmlBlockTag.showLookup(
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
    param: String,
    lookup: Lookup<T>,
    label: String,
    start: Int,
    end: Int,
    selectValue: HtmlBlockTag.(String, T) -> Unit,
) {
    val previousOwnersParam = combine(param, HISTORY)
    var minUntil = start

    showDetails(label, true) {
        selectInt("Previously", lookup.previousEntries.size, 0, 100, 1, previousOwnersParam)

        showListWithIndex(lookup.previousEntries) { index, previous ->
            val previousParam = combine(previousOwnersParam, index)
            selectValue(previousParam, previous.value)
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

        selectValue(combine(param, CURRENT), lookup.current)
    }
}

fun <T> HtmlBlockTag.editLookupTable(
    param: String,
    lookup: Lookup<T>,
    start: Int,
    end: Int,
    columns: List<Pair<String, HtmlBlockTag.(String, T) -> Unit>>,
) {
    val previousOwnersParam = combine(param, HISTORY)
    var minUntil = start

    selectInt("Previously", lookup.previousEntries.size, 0, 100, 1, previousOwnersParam)

    table {
        tr {
            th { +"Until" }
            columns.forEach { (label, _) ->
                th { +label }
            }
        }
        lookup.previousEntries.withIndex().forEach { (index, entry) ->
            val previousParam = combine(previousOwnersParam, index)

            tr {
                td {
                    selectInt(
                        "Until",
                        entry.until,
                        minUntil,
                        end,
                        1,
                        combine(previousParam, DATE),
                    )
                }
                columns.forEach { (_, selectValue) ->
                    td {
                        selectValue(previousParam, entry.value)
                    }
                }
            }

            minUntil = entry.until + 1
        }

        tr {
            tdString(">")
            columns.forEach { (_, selectValue) ->
                td {
                    selectValue(combine(param, CURRENT), lookup.current)
                }
            }
        }
    }
}

// parse

fun <T> parseLookup(
    parameters: Parameters,
    param: String,
    start: Int,
    parseValue: (String) -> T,
) = Lookup(
    parseValue(combine(param, CURRENT)),
    parseLookupEntries(parameters, param, start, parseValue),
)

private fun <T> parseLookupEntries(
    parameters: Parameters,
    param: String,
    start: Int,
    parseValue: (String) -> T,
): List<LookupEntry<T>> {
    val historyParam = combine(param, HISTORY)
    val count = parseInt(parameters, historyParam, 0)
    var minDate = start

    return (0..<count)
        .map {
            val previousOwner = parseLookupEntry(parameters, combine(historyParam, it), minDate, parseValue)
            minDate = previousOwner.until + 1

            previousOwner
        }
}

private fun <T> parseLookupEntry(
    parameters: Parameters,
    param: String,
    minDate: Int,
    parseValue: (String) -> T,
) = LookupEntry(
    parseValue(param),
    parseInt(parameters, combine(param, DATE), minDate),
)
