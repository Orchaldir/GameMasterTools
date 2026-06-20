package at.orchaldir.gm.utils.map

import org.junit.jupiter.api.Assertions.assertEquals

fun <TILE> assertTilemap(tilemap: TileMap2d<TILE>, size: MapSize2d, tile: TILE) {
    assertEquals(size, tilemap.size)
    assertEquals(size.tiles(), tilemap.tiles.size)

    tilemap.tiles.forEach {
        assertEquals(tile, it)
    }
}

fun <TILE> assertTilemap(tilemap: TileMap2d<TILE>, size: MapSize2d, tiles: List<List<TILE>>) {
    assertEquals(size, tilemap.size)
    assertEquals(size.tiles(), tilemap.tiles.size)
    assertEquals(size.height, tiles.size)

    repeat(size.height) { y ->
        val expectedRow = tiles[y]
        val row = tilemap.getRow(y)

        assertEquals(expectedRow.size, row.size, "Size of row $y doesn't match!")
        assertEquals(expectedRow, row, "Row $y doesn't match!")
    }
}