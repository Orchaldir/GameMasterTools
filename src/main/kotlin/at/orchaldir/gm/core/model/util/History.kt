package at.orchaldir.gm.core.model.util

import kotlinx.serialization.Serializable

@Serializable
data class History<T>(
    val current: T,
    val previousEntries: List<HistoryEntry<T>> = emptyList(),
) {
    constructor(current: T) : this(current, emptyList())

    constructor(current: T, previousEntry: HistoryEntry<T>) : this(current, listOf(previousEntry))

}
