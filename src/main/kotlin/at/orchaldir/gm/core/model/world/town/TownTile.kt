package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.world.terrain.PlainTerrain
import at.orchaldir.gm.core.model.world.terrain.Terrain
import kotlinx.serialization.Serializable

@Serializable
data class TownTile(
    val terrain: Terrain = PlainTerrain,
    val construction: Construction = NoConstruction,
) {

    fun canBuild() = construction is NoConstruction

}