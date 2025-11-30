package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Line2d(val points: List<Point2d>) {

    constructor(a: Point2d, b: Point2d) : this(listOf(a, b))

    init {
        require(points.size >= 2) { "The polygon has less than 2 corners!" }
    }

    fun calculateOrientation(index: Int) = if (index == 0) {
        points[0].calculateOrientation(points[1])
    } else if (index == points.lastIndex) {
        points[index - 1].calculateOrientation(points[index])
    } else {
        val point0 = points[index - 1]
        val point1 = points[index]
        val point2 = points[index + 1]
        val o0 = point0.calculateOrientation(point1)
        val o1 = point1.calculateOrientation(point2)
        ((o0 + o1) / 2.0f).normalizeZeroToTwoPi()
    }
}

data class Line2dBuilder(
    private val points: MutableList<Point2d> = mutableListOf(),
) {

    fun isValid() = points.size >= 2

    fun addMirroredPoints(aabb: AABB, width: Factor, vertical: Factor): Line2dBuilder {
        val (left, right) = aabb.getMirroredPoints(width, vertical)
        points.add(left)
        points.add(right)

        return this
    }

    fun addPoint(aabb: AABB, horizontal: Factor, vertical: Factor) = addPoint(aabb.getPoint(horizontal, vertical))

    fun addPoint(point: Point2d): Line2dBuilder {
        points.add(point)

        return this
    }

    fun build() = Line2d(points)
}
