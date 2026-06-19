package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.visualization.GridSize
import at.orchaldir.gm.utils.map.MapPoint2d
import at.orchaldir.gm.utils.map.MapSize2d
import kotlin.math.ceil

data class Borders(
    val bottom: Boolean = true,
    val left: Boolean = true,
    val right: Boolean = true,
    val top: Boolean = true,
    val tileX: Int = 0,
    val tileY: Int = 0,
) {
    constructor(isBorder: Boolean, tileX: Int = 0, tileY: Int = 0) :
            this(isBorder, isBorder, isBorder, isBorder, tileX, tileY)

    // TODO: remove old
    fun apply(size: MapSize2d) = MapSize2d(
        getWidth(size),
        getHeight(size),
    )

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

    fun limitWidth(size: MapSize2d, x: Int, width: Int): Int = if (right) {
        width.coerceAtMost(size.width - x)
    } else {
        width
    }

    fun limitHeight(size: MapSize2d, y: Int, height: Int): Int = if (bottom) {
        height.coerceAtMost(size.height - y)
    } else {
        height
    }

    fun calculateEvenOffsetX(gridSize: GridSize, length: Int) = calculateEvenOffset(tileX, gridSize.width(), length)

    fun calculateEvenOffsetY(gridSize: GridSize, length: Int) = calculateEvenOffset(tileY, gridSize.height(), length)

    fun calculateTileOffset(gridSize: MapSize2d, tile: MapSize2d) = MapPoint2d(
        calculateTileOffsetX(gridSize.width, tile.width),
        calculateTileOffsetY(gridSize.height, tile.height),
    )

    fun calculateTileOffsetX(gridSize: GridSize, length: Int) =
        calculateTileOffsetX(gridSize.width(), length)

    fun calculateTileOffsetX(width: Int, length: Int) =
        calculateTileOffset(tileX, width, length)

    fun calculateTileOffsetY(height: Int, length: Int) =
        calculateTileOffset(tileY, height, length)
}

fun calculateEvenOffset(position: Int, size: Int, length: Int) =
    ceil(size * position / length.toFloat()).toInt() % 2

fun calculateTileOffset(
    position: Int,
    gridSize: Int,
    length: Int,
): Int {
    val blockStart = position * gridSize
    val numBricks = ceil(blockStart / length.toFloat()).toInt()
    return numBricks * length - blockStart
}