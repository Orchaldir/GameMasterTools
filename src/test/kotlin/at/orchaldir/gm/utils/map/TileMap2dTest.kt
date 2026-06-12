package at.orchaldir.gm.utils.map

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TileMap2dTest {

    @Test
    fun `Test constructor with default value`() {
        val size = MapSize2d(3, 2)
        val tilemap = TileMap2d(size, 42)

        assertTilemap(tilemap, size, 42)
    }

}

fun <TILE> assertTilemap(tilemap: TileMap2d<TILE>, size: MapSize2d, tile: TILE) {
    assertEquals(size, tilemap.size)
    assertEquals(size.tiles(), tilemap.tiles.size)

    tilemap.tiles.forEach {
        assertEquals(tile, it)
    }
}