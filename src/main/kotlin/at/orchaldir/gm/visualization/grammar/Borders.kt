package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.utils.map.MapSize2d

data class Borders(
    val bottom: Boolean = true,
    val left: Boolean = true,
    val right: Boolean = true,
    val top: Boolean = true,
) {
    constructor(isBorder: Boolean): this(isBorder, isBorder, isBorder, isBorder)

    fun apply(size: MapSize2d) = MapSize2d(
        if (left) {
            size.width
        } else {
            Int.MAX_VALUE
        },
        if (bottom) {
            size.height
        } else {
            Int.MAX_VALUE
        },
    )
}
