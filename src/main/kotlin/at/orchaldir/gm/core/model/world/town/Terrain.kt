package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TerrainType {
    Hill,
    Mountain,
    Plain,
    River,
}

@Serializable
sealed class Terrain {

    fun <ID : Id<ID>> contains(id: ID) = when (this) {
        is HillTerrain -> mountain == id
        is MountainTerrain -> mountain == id
        is RiverTerrain -> river == id
        else -> false
    }

    fun getRiver() = when (this) {
        is RiverTerrain -> river
        else -> null
    }

    fun getMountain() = when (this) {
        is MountainTerrain -> mountain
        is HillTerrain -> mountain
        else -> null
    }
}

@Serializable
@SerialName("Hill")
data class HillTerrain(val mountain: RegionId) : Terrain()

@Serializable
@SerialName("Mountain")
data class MountainTerrain(val mountain: RegionId) : Terrain()

@Serializable
@SerialName("Plain")
data object PlainTerrain : Terrain()

@Serializable
@SerialName("River")
data class RiverTerrain(val river: RiverId) : Terrain()


