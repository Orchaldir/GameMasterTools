package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

@Serializable
data class TownTile(
    val terrain: Terrain = PlainTerrain,
) {

    fun <ID : Id<ID>> contains(river: ID) = when (terrain) {
        is RiverTerrain -> terrain.river == river
        else -> false
    }
}
