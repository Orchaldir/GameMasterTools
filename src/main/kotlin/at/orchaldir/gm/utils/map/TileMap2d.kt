package at.orchaldir.gm.utils.map

import at.orchaldir.gm.utils.map.MapSize2d.Companion.square
import kotlinx.serialization.Serializable

@Serializable
data class TileMap2d<TILE>(
    val size: MapSize2d,
    val tiles: List<TILE>,
) {
    constructor(size2d: MapSize2d, tile: TILE) : this(size2d, createTiles(size2d, tile))

    constructor(tile: TILE) : this(square(1), tile)

    init {
        require(size.tiles() == tiles.size) { "The number of tiles must match the map size" }
    }

    fun resize(
        widthStart: Int,
        widthEnd: Int,
        heightStart: Int,
        heightEnd: Int,
        tile: TILE,
    ): TileMap2d<TILE> {
        val newSize = MapSize2d(size.width + widthStart + widthEnd, size.height + heightStart + heightEnd)
        val newTiles = createTiles(newSize, tile).toMutableList()

        for (y in 0..<(size.height)) {
            val newY = y + heightStart

            if (!newSize.isYInside(newY)) {
                continue
            }

            for (x in 0..<(size.width)) {
                val newX = x + widthStart

                if (!newSize.isXInside(newX)) {
                    continue
                }

                val index = size.toIndexRisky(x, y)
                val newIndex = newSize.toIndexRisky(newX, newY)

                newTiles[newIndex] = tiles[index]
            }
        }

        return TileMap2d(newSize, newTiles)
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

private fun <TILE> createTiles(size2d: MapSize2d, tile: TILE) = (1..size2d.tiles()).map { tile }
