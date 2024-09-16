package at.orchaldir.gm.visualization.tile

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Color.Black
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.LineOptions
import at.orchaldir.gm.utils.renderer.Renderer

data class TileMap2dRenderer(
    val tileSize: Distance,
    val borderSize: Distance,
) {

    fun <TILE> calculateMapSize(map: TileMap2d<TILE>) = Size2d(
        tileSize * map.size.width,
        tileSize * map.size.height,
    )

    fun <TILE> render(
        map: TileMap2d<TILE>,
        renderTile: (Int, Int, Int, AABB, TILE) -> Unit,
    ) {
        val size = map.size
        val tileSize = Size2d.square(tileSize)
        var index = 0

        repeat(size.height) { y ->
            repeat(size.width) { x ->
                map.getTile(index)?.let { tile ->
                    val position = calculateTilePosition(x, y)
                    renderTile(index, x, y, AABB(position, tileSize), tile)
                }

                index++
            }
        }
    }

    fun <TILE> render(
        renderer: Renderer,
        map: TileMap2d<TILE>,
        lookup: (TILE) -> Color,
    ) {
        val lineStyle = LineOptions(Black.toRender(), borderSize)

        render(map) { _, _, _, aabb, tile ->
            val color = lookup(tile)
            val style = FillAndBorder(color.toRender(), lineStyle)

            renderer.renderRectangle(aabb, style)
        }
    }

    private fun calculateTilePosition(x: Int, y: Int) = Point2d(tileSize.value * x, tileSize.value * y)
}
