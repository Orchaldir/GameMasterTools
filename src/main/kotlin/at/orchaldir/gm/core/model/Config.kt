package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.economy.EconomyConfig
import at.orchaldir.gm.core.model.rpg.RpgConfig
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.utils.math.unit.AreaUnit
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val economy: EconomyConfig = EconomyConfig(),
    val rpg: RpgConfig = RpgConfig(),
    val time: Time = Time(),
    val largeAreaUnit: AreaUnit = AreaUnit.Hectare,
)
