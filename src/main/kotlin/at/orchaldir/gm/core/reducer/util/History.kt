package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.HistoryEntry
import at.orchaldir.gm.core.selector.time.getDefaultCalendar

fun <T> checkHistory(
    state: State,
    history: History<T>,
    startDate: Date?,
    noun: String,
    checkEntry: (State, T, String, Date?) -> Unit,
) {
    val calendar = state.getDefaultCalendar()
    var min = startDate
    var previous: HistoryEntry<T>? = null

    history.previousEntries.withIndex().forEach { (index, entry) ->
        val previousNoun = createPreviousNoun(index, noun)
        checkEntry(state, entry.entry, previousNoun, min)
        require(calendar.compareToOptional(entry.until, min) > 0) { "$previousNoun's until is too early!" }

        compareWithPreviousValue(previous, entry.entry, noun)
        previous = entry

        min = entry.until
    }

    compareWithPreviousValue(previous, history.current, noun)

    checkEntry(state, history.current, noun, min)
}

private fun <T> compareWithPreviousValue(
    previous: HistoryEntry<T>?,
    value: T,
    noun: String,
) {
    if (previous != null) {
        require(previous.entry != value) { "Cannot have the same $noun 2 times in a row!" }
    }
}

fun <T> checkHistory(
    state: State,
    history: History<T>,
    noun: String,
    checkEntry: (State, T, String) -> Unit,
) {
    history.previousEntries.withIndex().forEach { (index, previous) ->
        val previousNoun = createPreviousNoun(index, noun)
        checkEntry(state, previous.entry, previousNoun)
    }

    checkEntry(state, history.current, noun)
}

private fun createPreviousNoun(index: Int, noun: String) = "${index + 1}.previous $noun"
