package at.orchaldir.gm.utils.math

data class Point2d(val x: Float, val y: Float) {

    operator fun plus(other: Point2d) = Point2d(x + other.x, y + other.y)
    operator fun plus(size: Size2d) = Point2d(x + size.width, y + size.height)

}
