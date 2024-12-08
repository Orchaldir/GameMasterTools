package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.getDefaultCalendar

fun <T> checkHistory(
    state: State,
    history: History<T>,
    startDate: Date,
    noun: String,
    checkEntry: (State, T, String, Date) -> Unit,
) {
    val calendar = state.getDefaultCalendar()
    var min = startDate

    history.previousEntries.withIndex().forEach { (index, previous) ->
        val previousNoun = createPreviousNoun(index, noun)
        checkEntry(state, previous.entry, previousNoun, min)
        require(calendar.compareTo(previous.until, min) > 0) { "$previousNoun's until is too early!" }

        min = previous.until
    }

    checkEntry(state, history.current, noun, min)
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
