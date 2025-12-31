package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.economy.EconomyData
import at.orchaldir.gm.core.model.rpg.RpgData
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.utils.math.unit.AreaUnit
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val economy: EconomyData = EconomyData(),
    val rpg: RpgData = RpgData(),
    val time: Time = Time(),
    val largeAreaUnit: AreaUnit = AreaUnit.Hectare,
)
