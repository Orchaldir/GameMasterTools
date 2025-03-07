package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Polygon2d(val corners: List<Point2d>) {

    init {
        require(corners.size > 2) { "The polygon has less than 3 corners!" }
    }
}

data class Polygon2dBuilder(
    private val leftCorners: MutableList<Point2d> = mutableListOf(),
    private val rightCorners: MutableList<Point2d> = mutableListOf(),
) {


    fun addMirroredPoints(aabb: AABB, width: Factor, vertical: Factor, isSharp: Boolean = false) {
        val (left, right) = aabb.getMirroredPoints(width, vertical)

        addPoints(left, right, isSharp)
    }

    fun addPoints(left: Point2d, right: Point2d, isSharp: Boolean = false) {
        leftCorners.add(left)
        rightCorners.add(right)

        if (isSharp) {
            leftCorners.add(left)
            rightCorners.add(right)
        }
    }

    fun addPoint(aabb: AABB, horizontal: Factor, vertical: Factor, isSharp: Boolean = false) {
        addPoint(aabb.getPoint(horizontal, vertical), isSharp)
    }

    fun addPoint(point: Point2d, isSharp: Boolean = false) {
        leftCorners.add(point)

        if (isSharp) {
            leftCorners.add(point)
        }
    }

    fun createSharpCorners(index: Int): Boolean {
        if (!createSharpCorner(leftCorners, index)) {
            return false
        }

        return createSharpCorner(rightCorners, index)
    }

    private fun createSharpCorner(corners: MutableList<Point2d>, index: Int): Boolean {
        val corner = corners.getOrNull(index)

        if (corner != null) {
            corners.add(index, corner)

            return true
        }

        return false
    }

    fun reverse() {
        leftCorners.reverse()
        rightCorners.reverse()
    }

    fun build() = Polygon2d(leftCorners + rightCorners.reversed())
}
