package at.orchaldir.gm.utils.map

import at.orchaldir.gm.utils.map.MapSize2d.Companion.square
import kotlinx.serialization.Serializable

@Serializable
data class TileMap2d<TILE>(
    val size: MapSize2d,
    val tiles: List<TILE>,
) {
    constructor(size2d: MapSize2d, tile: TILE) : this(size2d, (1..size2d.tiles()).map { tile })
    constructor(tile: TILE) : this(square(1), tile)

    init {
        require(size.tiles() == tiles.size) { "The number of tiles must match the map size" }
    }

    fun isInside(index: Int) = size.isInside(index)

    fun getTile(index: Int) = tiles.getOrNull(index)

    fun contains(check: (TILE) -> Boolean) = tiles.any { check(it) }
}
