package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.MapSize2d.Companion.square
import kotlinx.serialization.Serializable

@Serializable
data class BuildingLot(
    val town: TownMapId = TownMapId(0),
    val tileIndex: Int = 0,
    val size: MapSize2d = square(1),
)