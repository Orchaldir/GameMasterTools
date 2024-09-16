package at.orchaldir.gm.utils.map

import kotlinx.serialization.Serializable

@Serializable
data class TileMap2d<TILE>(
    val size2d: MapSize2d,
    val tiles: List<TILE>,
) {
    constructor(size2d: MapSize2d, tile: TILE) : this(size2d, (1..size2d.tiles()).map { tile })

    init {
        require(size2d.tiles() == tiles.size) { "The number of tiles must match the map size" }
    }
}
