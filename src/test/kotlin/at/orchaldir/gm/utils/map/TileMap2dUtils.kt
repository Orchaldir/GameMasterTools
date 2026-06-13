package at.orchaldir.gm.utils.map

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

fun <TILE> assertTilemap(tilemap: TileMap2d<TILE>, size: MapSize2d, tile: TILE) {
    assertEquals(size, tilemap.size)
    assertEquals(size.tiles(), tilemap.tiles.size)

    tilemap.tiles.forEach {
        assertEquals(tile, it)
    }
}

fun <TILE> assertTilemap(tilemap: TileMap2d<TILE>, size: MapSize2d, tiles: List<TILE>) {
    assertEquals(size, tilemap.size)
    assertEquals(size.tiles(), tilemap.tiles.size)

    assertEquals(tiles, tilemap.tiles)
}