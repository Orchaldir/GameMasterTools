package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.util.Lookup
import at.orchaldir.gm.core.model.util.LookupEntry
import io.ktor.http.*
import kotlinx.html.*

// show

fun <T> HtmlBlockTag.showLookupTable(
    lookup: Lookup<T>,
    column: Pair<String, HtmlBlockTag.(T) -> Unit>,
) = showLookupTable(lookup, listOf(column))

fun <T> HtmlBlockTag.showLookupTable(
    lookup: Lookup<T>,
    columns: List<Pair<String, HtmlBlockTag.(T) -> Unit>>,
) {
    table {
        tr {
            th { +"Until" }
            columns.forEach { (label, _) ->
                th { +label }
            }
        }
        lookup.entries.forEach { entry ->
            tr {
                tdInt(entry.until)
                columns.forEach { (_, showValue) ->
                    td {
                        showValue(entry.value)
                    }
                }
            }
        }
    }
}

// edit

fun <T> HtmlBlockTag.editLookupTable(
    param: String,
    lookup: Lookup<T>,
    minEntries: Int,
    maxEntries: Int,
    startUntil: Int,
    column: Pair<String, HtmlBlockTag.(String, T) -> Unit>,
) = editLookupTable(
    param,
    lookup,
    minEntries,
    maxEntries,
    startUntil,
    listOf(column),
)

fun <T> HtmlBlockTag.editLookupTable(
    param: String,
    lookup: Lookup<T>,
    minEntries: Int,
    maxEntries: Int,
    startUntil: Int,
    columns: List<Pair<String, HtmlBlockTag.(String, T) -> Unit>>,
) {
    var minUntil = startUntil

    selectInt(
        "Entries",
        lookup.entries.size,
        minEntries,
        maxEntries,
        1,
        combine(param, NUMBER),
    )

    table {
        tr {
            th { +"Until" }
            columns.forEach { (label, _) ->
                th { +label }
            }
        }
        lookup.entries.withIndex().forEach { (index, entry) ->
            val entryParam = combine(param, index)

            tr {
                td {
                    selectInt(
                        "Until",
                        entry.until,
                        minUntil,
                        Int.MAX_VALUE,
                        1,
                        combine(entryParam, DATE),
                    )
                }
                columns.forEach { (_, selectValue) ->
                    td {
                        selectValue(entryParam, entry.value)
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

    return Lookup(
        parseList<LookupEntry<T>>(parameters, param, 0) { _, entryParam ->
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
