package at.orchaldir.gm.core.model.world.settlement

import at.orchaldir.gm.core.model.world.building.BuildingId
import kotlinx.serialization.Serializable

@Serializable
data class SettlementTile(
    val terrain: Terrain = PlainTerrain,
    val construction: Construction = NoConstruction,
) {

    fun canBuild() = construction is NoConstruction

    fun canResize(building: BuildingId) = construction is NoConstruction ||
            (construction is BuildingTile && construction.building == building)

}