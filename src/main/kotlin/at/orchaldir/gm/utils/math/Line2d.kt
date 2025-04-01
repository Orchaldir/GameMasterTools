package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Line2d(val points: List<Point2d>) {

    init {
        require(points.size > 2) { "The polygon has less than 3 corners!" }
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
