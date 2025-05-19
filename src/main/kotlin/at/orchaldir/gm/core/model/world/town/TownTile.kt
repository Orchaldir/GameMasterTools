package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.world.building.BuildingId
import kotlinx.serialization.Serializable

@Serializable
data class TownTile(
    val terrain: Terrain = PlainTerrain,
    val construction: Construction = NoConstruction,
) {

    fun canBuild() = construction is NoConstruction

    fun canResize(building: BuildingId) = construction is NoConstruction ||
            (construction is BuildingTile && construction.building == building)

}