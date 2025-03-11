package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.time.date.Date
import kotlinx.serialization.Serializable

@Serializable
data class HistoryEntry<T>(
    val entry: T,
    val until: Date,
)
