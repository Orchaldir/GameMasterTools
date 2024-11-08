package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.time.Date
import kotlinx.serialization.Serializable

@Serializable
data class HistoryEntry(
    val entry: Owner,
    val until: Date,
)
