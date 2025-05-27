package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE
import kotlinx.serialization.Serializable

@Serializable
data class Polygon2d(val corners: List<Point2d>) {

    constructor(aabb: AABB) : this(aabb.getCorners())

    init {
        require(corners.size > 2) { "The polygon has less than 3 corners!" }
    }
}

data class Polygon2dBuilder(
    private val leftCorners: MutableList<Point2d> = mutableListOf(),
    private val rightCorners: MutableList<Point2d> = mutableListOf(),
) {

    fun isValid() = leftCorners.size + rightCorners.size >= 3

    fun addMirroredPoints(aabb: AABB, width: Factor, vertical: Factor, isSharp: Boolean = false): Polygon2dBuilder {
        val (left, right) = aabb.getMirroredPoints(width, vertical)

        return addPoints(left, right, isSharp)
    }

    fun addHorizontalPoints(
        aabb: AABB,
        width: Factor,
        horizontal: Factor,
        vertical: Factor,
        isSharp: Boolean = false,
    ): Polygon2dBuilder {
        val half = width / 2.0f
        val left = aabb.getPoint(horizontal - half, vertical)
        val right = aabb.getPoint(horizontal + half, vertical)

        return addPoints(left, right, isSharp)
    }

    fun addLeftAndRightPoint(
        center: Point2d,
        orientation: Orientation,
        halfWidth: Distance,
    ): Polygon2dBuilder {
        val right = center.createPolar(halfWidth, orientation - QUARTER_CIRCLE)
        val left = center.createPolar(halfWidth, orientation + QUARTER_CIRCLE)

        return addPoints(left, right)
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

    fun addLeftPoint(aabb: AABB, horizontal: Factor, vertical: Factor, isSharp: Boolean = false) =
        addLeftPoint(aabb.getPoint(horizontal, vertical), isSharp)

    fun addLeftPoint(point: Point2d, isSharp: Boolean = false): Polygon2dBuilder {
        leftCorners.add(point)

        if (isSharp) {
            leftCorners.add(point)
        }

        return this
    }

    fun addRightPoint(aabb: AABB, horizontal: Factor, vertical: Factor, isSharp: Boolean = false) =
        addRightPoint(aabb.getPoint(horizontal, vertical), isSharp)

    fun addRightPoint(point: Point2d, isSharp: Boolean = false): Polygon2dBuilder {
        rightCorners.add(point)

        if (isSharp) {
            rightCorners.add(point)
        }

        return this
    }

    fun addVerticallyMirroredPoint(aabb: AABB, left: Point2d, isSharp: Boolean = false): Polygon2dBuilder {
        val right = aabb.mirrorVertically(left)

        return addPoints(left, right, isSharp)
    }

    fun addRectangle(aabb: AABB): Polygon2dBuilder {
        val (topLeft, topRight, bottomRight, bottomLeft) = aabb.getCorners()

        addPoints(bottomLeft, bottomRight)
        addPoints(topLeft, topRight)

        return this
    }

    fun addSquare(center: Point2d, size: Distance): Polygon2dBuilder {
        val half = size / 2.0f
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

    fun reverse(): Polygon2dBuilder {
        leftCorners.reverse()
        rightCorners.reverse()

        return this
    }

    fun build() = Polygon2d(leftCorners + rightCorners.reversed())
}
