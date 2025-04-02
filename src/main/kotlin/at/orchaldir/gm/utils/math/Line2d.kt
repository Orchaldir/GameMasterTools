package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable
import kotlin.math.atan
import kotlin.math.atan2

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
        (points[index + 1].calculateOrientation() - points[index].calculateOrientation()).normalizeZeroToTwoPi()
        /*
        val point0 = points[index-1]
        val point1 = points[index]
        val point2 = points[index+1]
        val slope01 = point0.calculateSlope(point1)
        val slope12 = point1.calculateSlope(point2)
        Orientation.fromRadians(atan((slope12-slope01)/(1.0f + slope12 * slope01)))
         */
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
