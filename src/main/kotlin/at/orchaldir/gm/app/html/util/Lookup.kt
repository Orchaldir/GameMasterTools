package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.HISTORY
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.util.Lookup
import at.orchaldir.gm.core.model.util.LookupEntry
import io.ktor.http.*
import kotlinx.html.*

// show

fun <T> HtmlBlockTag.showLookupDetails(
    lookup: Lookup<T>,
    label: String,
    showValue: HtmlBlockTag.(T) -> Unit,
) {
    showDetails(label, true) {
        showLookup( lookup, showValue)
    }
}

fun <T> HtmlBlockTag.showLookup(
    lookup: Lookup<T>,
    showValue: HtmlBlockTag.(T) -> Unit,
) {
    showList( lookup.entries) { previous ->
        +"Until ${previous.until}: "
        showValue(previous.value)
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
        editList(previousOwnersParam, lookup.entries, 0, 100, 1) { index, param, entry ->
            val entryParam = combine(previousOwnersParam, index)

            selectValue(entryParam, entry.value)
            selectInt(
                "Until",
                entry.until,
                minUntil,
                end,
                1,
                combine(entryParam, DATE),
            )

            minUntil = entry.until + 1
        }
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

    selectInt("Previously", lookup.entries.size, 0, 100, 1, previousOwnersParam)

    table {
        tr {
            th { +"Until" }
            columns.forEach { (label, _) ->
                th { +label }
            }
        }
        lookup.entries.withIndex().forEach { (index, entry) ->
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
    }
}

// parse

fun <T> parseLookup(
    parameters: Parameters,
    param: String,
    start: Int,
    parseValue: (String) -> T,
): Lookup<T> {
    var minDate = start

    return Lookup<T>(
        parseList<LookupEntry<T>>(parameters, combine(param, HISTORY), 0) { _, entryParam ->
            val entry = parseLookupEntry(parameters, entryParam, minDate, parseValue)

            minDate = entry.until + 1

            entry
        }
    )
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
