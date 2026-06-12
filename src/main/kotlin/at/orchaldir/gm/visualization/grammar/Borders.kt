package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.logger
import at.orchaldir.gm.core.model.visualization.GridSize
import at.orchaldir.gm.utils.map.MapSize2d
import kotlin.math.ceil

data class Borders(
    val bottom: Boolean = true,
    val left: Boolean = true,
    val right: Boolean = true,
    val top: Boolean = true,
    val x: Int = 0,
    val y: Int = 0,
) {
    constructor(isBorder: Boolean, x: Int = 0, y: Int = 0):
            this(isBorder, isBorder, isBorder, isBorder, x, y)

    fun apply(size: MapSize2d) = MapSize2d(
        getWidth(size),
        getHeight(size),
    )

    fun applyBottomAndRight(size: MapSize2d) = MapSize2d(getWidth(size), getHeight(size))

    fun applyRight(size: MapSize2d) = size.copy(width = getWidth(size))
    fun applyBottom(size: MapSize2d) = size.copy(height = getHeight(size))

    private fun getWidth(size: MapSize2d): Int = if (right) {
        size.width
    } else {
        Int.MAX_VALUE
    }

    private fun getHeight(size: MapSize2d): Int = if (bottom) {
        size.height
    } else {
        Int.MAX_VALUE
    }

    fun calculateTileOffset(gridSize: MapSize2d, tile: MapSize2d) = MapSize2d(
        calculateTileOffsetX(gridSize.width, tile.width),
        calculateTileOffsetY(gridSize.height, tile.height),
    )

    fun calculateTileOffsetX(gridSize: GridSize, length: Int) =
        calculateTileOffsetX(gridSize.width(), length)

    fun calculateTileOffsetX(gridSize: MapSize2d, length: Int) =
        calculateTileOffsetX(gridSize.width, length)

    private fun calculateTileOffsetX(width: Int, length: Int) =
        calculateTileOffsetY(left, x, width, length)

    private fun calculateTileOffsetY(height: Int, length: Int) =
        calculateTileOffsetY(top, y, height, length)
}

fun calculateTileOffsetY(
    border: Boolean,
    position: Int,
    gridSize: Int,
    length: Int,
): Int {
    if (border) {
        return 0
    }

    val blockStart = position * gridSize
    val numBricks = ceil(blockStart / length.toFloat()).toInt()
    val offset = numBricks * length - blockStart

    logger.info { "position=${position} blockStart=$blockStart numBricks=$numBricks offset=$offset" }

    return offset
}
