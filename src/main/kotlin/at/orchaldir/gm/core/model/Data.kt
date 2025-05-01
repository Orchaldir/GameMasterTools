package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.selector.economy.Economy
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val economy: Economy = Economy(),
    val time: Time = Time(),
)
