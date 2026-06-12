package at.orchaldir.gm.utils.map

import kotlinx.serialization.Serializable

@Serializable
data class MapPoint2d(val x: Int, val y: Int) {


    fun format() = "($x x $y)"

    operator fun plus(other: MapPoint2d) = MapPoint2d(x + other.x, y + other.y)
    operator fun minus(other: MapPoint2d) = MapPoint2d(x - other.x, y - other.y)

}
