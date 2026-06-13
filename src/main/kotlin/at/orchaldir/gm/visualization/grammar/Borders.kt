package at.orchaldir.gm.visualization.grammar

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
    constructor(isBorder: Boolean, x: Int = 0, y: Int = 0) :
            this(isBorder, isBorder, isBorder, isBorder, x, y)

    fun apply(size: MapSize2d) = MapSize2d(
        getWidth(size),
        getHeight(size),
    )

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

    fun calculateEvenOffsetX(gridSize: GridSize, length: Int) = calculateEvenOffset(x, gridSize, length)

    fun calculateEvenOffsetY(gridSize: GridSize, length: Int) = calculateEvenOffset(y, gridSize, length)

    fun calculateTileOffset(gridSize: MapSize2d, tile: MapSize2d) = MapSize2d(
        calculateTileOffsetX(gridSize.width, tile.width),
        calculateTileOffsetY(gridSize.height, tile.height),
    )

    fun calculateTileOffset2(gridSize: MapSize2d, tile: MapSize2d) = MapSize2d(
        calculateTileOffsetX2(gridSize.width, tile.width),
        calculateTileOffsetY(gridSize.height, tile.height),
    )

    // TODO: remove old
    fun calculateTileOffsetX(gridSize: GridSize, length: Int) =
        calculateTileOffsetX(gridSize.width(), length)

    fun calculateTileOffsetX2(gridSize: GridSize, length: Int) =
        calculateTileOffsetX2(gridSize.width(), length)

    fun calculateTileOffsetX(gridSize: MapSize2d, length: Int) =
        calculateTileOffsetX(gridSize.width, length)

    private fun calculateTileOffsetX(width: Int, length: Int) =
        calculateTileOffset(left, x, width, length)

    private fun calculateTileOffsetX2(width: Int, length: Int) =
        calculateTileOffset2(x, width, length)

    private fun calculateTileOffsetY(height: Int, length: Int) =
        calculateTileOffset(top, y, height, length)
}

fun calculateEvenOffset(position: Int, gridSize: GridSize, length: Int) =
    ceil(gridSize.height() * position / length.toFloat()).toInt() % 2

fun calculateTileOffset(
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
    return numBricks * length - blockStart
}

fun calculateTileOffset2(
    position: Int,
    gridSize: Int,
    length: Int,
): Int {
    val blockStart = position * gridSize
    val numBricks = ceil(blockStart / length.toFloat()).toInt()
    return blockStart - numBricks * length
}