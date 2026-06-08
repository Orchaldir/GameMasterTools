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
) {
    constructor(isBorder: Boolean, x: Int = 0): this(isBorder, isBorder, isBorder, isBorder, x)

    fun apply(size: MapSize2d) = MapSize2d(
        getWidth(size),
        getHeight(size),
    )

    fun applyLeft(size: MapSize2d) = size.copy(width = getWidth(size))
    fun applyBottom(size: MapSize2d) = size.copy(height = getHeight(size))

    private fun getWidth(size: MapSize2d): Int = if (left) {
        size.width
    } else {
        Int.MAX_VALUE
    }

    private fun getHeight(size: MapSize2d): Int = if (bottom) {
        size.height
    } else {
        Int.MAX_VALUE
    }

    fun calculateTileOffsetX(gridSize: GridSize, length: Int): Int {
        if (left) {
            return 0
        }

        val startBlockX = x * gridSize.width()
        val numBricksX = ceil(startBlockX / length.toFloat()).toInt()
        val offset = numBricksX * length - startBlockX

        logger.info { "x=${x} startBlockX=$startBlockX numBricksX=$numBricksX offset=$offset" }

        return offset
    }
}
