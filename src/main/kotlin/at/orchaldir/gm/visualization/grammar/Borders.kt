package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.utils.map.MapSize2d

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
}
