package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.Color.Black
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.TileMap2d
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.ZERO_DISTANCE
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.grammar.Borders

data class TileMap2dRenderer(
    val tileSize: Size2d,
    val borderSize: Distance = ZERO_DISTANCE,
) {
    constructor(width: Distance, borderSize: Distance = ZERO_DISTANCE) :
            this(Size2d.square(width), borderSize)

    constructor(area: Size2d, tiles: MapSize2d, borderSize: Distance = ZERO_DISTANCE) :
            this(area / tiles, borderSize)

    fun calculateAreaSize(area: MapSize2d) = tileSize * area
    fun <TILE> calculateMapSize(map: TileMap2d<TILE>) = calculateAreaSize(map.size)

    fun <TILE> render(
        map: TileMap2d<TILE>,
        renderTile: (Int, Int, Int, AABB, TILE) -> Unit,
    ) {
        val size = map.size
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
        map: TileMap2d<TILE>,
        start: Point2d,
        renderTile: (Int, AABB, Borders, TILE) -> Unit,
    ) {
        val size = map.size
        var index = 0
        val isTopBorders = MutableList(size.width) { true }

        repeat(size.height) { y ->
            var isLeftBorder = true
            var currentTile = map.getTile(0, y)

            repeat(size.width) { x ->
                val rightTile = map.getTile(x + 1, y)
                val bottomTile = map.getTile(x, y + 1)
                val isRightBorder = currentTile != rightTile
                val isTopBorder = isTopBorders[x]
                val isBottomBorder = currentTile != bottomTile

                currentTile?.let { tile ->
                    val position = start + calculateTilePosition(x, y)
                    val borders = Borders(
                        isBottomBorder,
                        isLeftBorder,
                        isRightBorder,
                        isTopBorder,
                        x,
                        y,
                    )

                    renderTile(index, AABB(position, tileSize), borders, tile)
                }

                index++
                currentTile = rightTile
                isLeftBorder = isRightBorder
                isTopBorders[x] = isBottomBorder
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

    fun <TILE> calculateTilePosition(map: TileMap2d<TILE>, index: Int) =
        calculateTilePosition(map.size.toX(index), map.size.toY(index))

    fun calculateTilePosition(x: Int, y: Int) = Point2d(tileSize.width * x, tileSize.height * y)
}
