package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.Color.Black
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions

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
        layerRenderer: MultiLayerRenderer,
        map: TileMap2d<TILE>,
        lookup: (TILE) -> Color,
    ) {
        val lineStyle = LineOptions(Black.toRender(), borderSize)
        val renderer = layerRenderer.getLayer()

        render(map) { _, _, _, aabb, tile ->
            val color = lookup(tile)
            val style = FillAndBorder(color.toRender(), lineStyle)

            renderer.renderRectangle(aabb, style)
        }
    }

    fun <TILE> renderWithLinksAndTooltips(
        renderer: AdvancedRenderer,
        map: TileMap2d<TILE>,
        colorLookup: (TILE) -> Color,
        linkLookup: (Int, TILE) -> String? = { _, _ -> null },
        tooltipLookup: (Int, TILE) -> String? = { _, _ -> null },
    ) {
        val lineStyle = LineOptions(Black.toRender(), borderSize)

        render(map) { index, _, _, aabb, tile ->
            val color = colorLookup(tile)
            val style = FillAndBorder(color.toRender(), lineStyle)

            renderer.optionalLinkAndTooltip(linkLookup(index, tile), tooltipLookup(index, tile)) {
                it.renderRectangle(aabb, style)
            }
        }
    }

    fun calculateLotSize(size: MapSize2d) = Size2d(tileSize * size.width, tileSize * size.height)

    fun <TILE> calculateTilePosition(map: TileMap2d<TILE>, index: Int) =
        calculateTilePosition(map.size.toX(index), map.size.toY(index))

    fun calculateTilePosition(x: Int, y: Int) = Point2d(tileSize * x, tileSize * y)
}
