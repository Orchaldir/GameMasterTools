package at.orchaldir.gm.utils.map

import kotlinx.serialization.Serializable

@Serializable
data class MapPoint2d(val x: Int = 0, val y: Int = 0) {

    fun format() = "($x x $y)"

    fun modifyX(modifier: Int) = MapPoint2d(x + modifier, y)
    fun modifyY(modifier: Int) = MapPoint2d(x, y + modifier)

    operator fun plus(other: MapPoint2d) = MapPoint2d(x + other.x, y + other.y)
    operator fun minus(other: MapPoint2d) = MapPoint2d(x - other.x, y - other.y)

    operator fun times(number: Int) = MapPoint2d(x * number, y * number)

    operator fun plus(size: MapSize2d) = MapPoint2d(x + size.width, y + size.height)
    operator fun minus(size: MapSize2d) = MapPoint2d(x - size.width, y - size.height)
    operator fun times(size: MapSize2d) = MapPoint2d(x * size.width, y * size.height)

}
