package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.model.world.terrain.PlainTerrain
import at.orchaldir.gm.core.model.world.terrain.Terrain
import kotlinx.serialization.Serializable

@Serializable
data class TownTile(
    val terrain: Terrain = PlainTerrain,
    val construction: Construction = NoConstruction,
) {

    fun canBuildBuilding() = construction is NoConstruction

    fun canBuildRailway() = construction !is BuildingTile

    fun canBuildStreet() = canBuildRailway()

    fun canResizeBuilding(building: BuildingId) = construction is NoConstruction ||
            (construction is BuildingTile && construction.building == building)

}