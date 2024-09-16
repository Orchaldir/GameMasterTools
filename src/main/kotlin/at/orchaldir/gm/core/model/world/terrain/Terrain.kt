package at.orchaldir.gm.core.model.world.terrain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Terrain

@Serializable
@SerialName("Hill")
data class HillTerrain(val mountain: MountainId) : Terrain()

@Serializable
@SerialName("Mountain")
data class MountainTerrain(val mountain: MountainId) : Terrain()

@Serializable
@SerialName("Plain")
data object PlainTerrain : Terrain()

@Serializable
@SerialName("River")
data class RiverTerrain(val river: RiverId) : Terrain()


