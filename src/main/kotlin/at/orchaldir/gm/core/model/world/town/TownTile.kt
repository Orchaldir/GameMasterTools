package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.world.terrain.*
import kotlinx.serialization.Serializable

@Serializable
data class TownTile(
    val terrain: Terrain = PlainTerrain,
)