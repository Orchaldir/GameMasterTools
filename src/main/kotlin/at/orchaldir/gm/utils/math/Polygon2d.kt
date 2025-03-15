package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.unit.Distance
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

    fun addMirroredPoints(aabb: AABB, width: Factor, vertical: Factor, isSharp: Boolean = false): Polygon2dBuilder {
        val (left, right) = aabb.getMirroredPoints(width, vertical)

        addPoints(left, right, isSharp)

        return this
    }

    fun addPoints(left: Point2d, right: Point2d, isSharp: Boolean = false): Polygon2dBuilder {
        leftCorners.add(left)
        rightCorners.add(right)

        if (isSharp) {
            leftCorners.add(left)
            rightCorners.add(right)
        }

        return this
    }

    fun addLeftPoint(aabb: AABB, horizontal: Factor, vertical: Factor, isSharp: Boolean = false): Polygon2dBuilder {
        addLeftPoint(aabb.getPoint(horizontal, vertical), isSharp)

        return this
    }

    fun addLeftPoint(point: Point2d, isSharp: Boolean = false): Polygon2dBuilder {
        leftCorners.add(point)

        if (isSharp) {
            leftCorners.add(point)
        }

        return this
    }

    fun addRightPoint(aabb: AABB, horizontal: Factor, vertical: Factor, isSharp: Boolean = false): Polygon2dBuilder {
        addRightPoint(aabb.getPoint(horizontal, vertical), isSharp)

        return this
    }

    fun addRightPoint(point: Point2d, isSharp: Boolean = false): Polygon2dBuilder {
        rightCorners.add(point)

        if (isSharp) {
            rightCorners.add(point)
        }

        return this
    }

    fun addRectangle(center: Point2d, halfWidth: Distance, halfHeight: Distance): Polygon2dBuilder {
        val bottomLeft = center.minusWidth(halfWidth).addHeight(halfHeight)
        val bottomRight = center.addWidth(halfWidth).addHeight(halfHeight)
        val centerLeft = center.minusWidth(halfWidth).minusHeight(halfHeight)
        val centerRight = center.addWidth(halfWidth).minusHeight(halfHeight)

        addPoints(bottomLeft, bottomRight)
        addPoints(centerLeft, centerRight)

        return this
    }

    fun addSquare(center: Point2d, half: Distance): Polygon2dBuilder {
        val bottomLeft = center.minusWidth(half).addHeight(half)
        val bottomRight = center.plus(half)
        val centerLeft = center.minus(half)
        val centerRight = center.addWidth(half).minusHeight(half)

        addPoints(bottomLeft, bottomRight)
        addPoints(centerLeft, centerRight)

        return this
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
