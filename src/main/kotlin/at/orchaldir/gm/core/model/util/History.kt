package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.time.date.Date
import kotlinx.serialization.Serializable

@Serializable
data class HistoryEntry<T>(
    val entry: T,
    val until: Date,
)

@Serializable
data class History<T>(
    val current: T,
    val previousEntries: List<HistoryEntry<T>> = emptyList(),
) {
    constructor(current: T) : this(current, emptyList())

    constructor(current: T, previousEntry: HistoryEntry<T>) : this(current, listOf(previousEntry))

}
