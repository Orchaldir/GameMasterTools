package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Line2d(val points: List<Point2d>) {

    init {
        require(points.size > 2) { "The polygon has less than 3 corners!" }
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

    fun addPoint(aabb: AABB, horizontal: Factor, vertical: Factor): Line2dBuilder {
        addPoint(aabb.getPoint(horizontal, vertical))

        return this
    }

    fun addPoint(point: Point2d): Line2dBuilder {
        points.add(point)

        return this
    }

    fun build() = Line2d(points)
}
