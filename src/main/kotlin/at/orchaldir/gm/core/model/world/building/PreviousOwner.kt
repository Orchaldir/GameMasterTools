package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.time.Date
import kotlinx.serialization.Serializable

@Serializable
data class PreviousOwner(
    val owner: Owner,
    val until: Date,
)
