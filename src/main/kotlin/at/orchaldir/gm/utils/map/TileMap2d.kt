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

    fun requireIsInside(index: Int) = require(size.isInside(index)) { "Tile $index is outside the map!" }

    fun requireIsInside(x: Int, y: Int) = require(size.isInside(x, y)) { "Tile ($x, $y) is outside the map!" }

    fun getRequiredTile(index: Int): TILE {
        requireIsInside(index)
        return tiles[index]
    }

    fun getRequiredTile(x: Int, y: Int): TILE {
        requireIsInside(x, y)
        return tiles[size.toIndexRisky(x, y)]
    }

    fun getTile(index: Int) = tiles.getOrNull(index)

    fun getTile(x: Int, y: Int) = size.toIndex(x, y)?.let { tiles[it] }

    fun contains(check: (TILE) -> Boolean) = tiles.any { check(it) }
}
