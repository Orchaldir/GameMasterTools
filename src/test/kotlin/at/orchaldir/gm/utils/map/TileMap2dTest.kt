package at.orchaldir.gm.utils.map

import org.junit.jupiter.api.Test

class TileMap2dTest {

    @Test
    fun `Test constructor with default value`() {
        val size = MapSize2d(3, 2)
        val tilemap = TileMap2d(size, 42)

        assertTilemap(tilemap, size, 42)
    }

}