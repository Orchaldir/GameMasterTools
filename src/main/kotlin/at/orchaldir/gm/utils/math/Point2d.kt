package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Point2d(val x: Float, val y: Float) {

    operator fun plus(distance: Distance) = Point2d(x + distance.value, y + distance.value)
    operator fun plus(other: Point2d) = Point2d(x + other.x, y + other.y)
    operator fun plus(size: Size2d) = Point2d(x + size.width, y + size.height)

    operator fun minus(size: Size2d) = Point2d(x - size.width, y - size.height)

}
