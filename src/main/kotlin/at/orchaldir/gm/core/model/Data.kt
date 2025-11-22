package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.economy.Economy
import at.orchaldir.gm.core.model.rpg.RpgData
import at.orchaldir.gm.core.model.time.Time
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val economy: Economy = Economy(),
    val rpg: RpgData = RpgData(),
    val time: Time = Time(),
)
