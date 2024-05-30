package at.orchaldir.gm.utils.math

data class Point2d(val x: Int, val y: Int) {

    operator fun plus(other: Point2d) = Point2d(x + other.x, y + other.y)
    operator fun plus(size: Size2d) = Point2d(x + size.width, y + size.height)

}
