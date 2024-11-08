package at.orchaldir.gm.core.model.util

import kotlinx.serialization.Serializable

@Serializable
data class History<T>(
    val current: T,
    val previousOwners: List<HistoryEntry<T>> = emptyList(),
) {
    constructor(owner: T) : this(owner, emptyList())

    constructor(owner: T, previousOwner: HistoryEntry<T>) : this(owner, listOf(previousOwner))

}
