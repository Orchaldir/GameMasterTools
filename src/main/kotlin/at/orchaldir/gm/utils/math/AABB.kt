package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.Factor.Companion.fromNumber
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable

private val TWO = fromPercentage(200)

/**
 * An axis aligned bounding box.
 */
@Serializable
data class AABB(val start: Point2d, val size: Size2d) {

    constructor(size: Size2d) : this(Point2d(), size)
    constructor(x: Float, y: Float, width: Distance, height: Distance) : this(
        Point2d.fromMeters(x, y),
        Size2d(width, height)
    )

    constructor(x: Float, y: Float, size: Size2d) : this(Point2d.fromMeters(x, y), size)

    companion object {
        fun fromBottom(bottom: Point2d, size: Size2d) = fromCenter(
            bottom.minusHeight(size.height / 2), size
        )

        fun fromCenter(center: Point2d, size: Size2d) = AABB(
            center - size / TWO, size
        )

        fun fromCenter(center: Point2d, size: Distance) = fromWidthAndHeight(center, size, size)

        fun fromTop(top: Point2d, size: Size2d) = fromCenter(
            top.addHeight(size.height / 2), size
        )

        fun fromCorners(start: Point2d, end: Point2d): AABB {
            val diff = end - start
            return AABB(
                start,
                Size2d(diff.x, diff.y),
            )
        }

        fun fromMeters(x: Float, y: Float, width: Float, height: Float) =
            AABB(Point2d.fromMeters(x, y), Size2d.fromMeters(width, height))

        fun fromRadius(center: Point2d, radius: Distance) =
            fromRadii(center, radius, radius)

        fun fromRadii(center: Point2d, radiusX: Distance, radiusY: Distance) =
            fromWidthAndHeight(center, radiusX * 2.0f, radiusY * 2.0f)

        fun fromWidthAndHeight(center: Point2d, width: Distance, height: Distance) =
            fromCenter(center, Size2d(width, height))
    }

    fun getCenter() = start + size / TWO

    fun getEnd() = start + size

    fun getInnerRadius() = size.innerRadius()

    fun convertWidth(factor: Factor) = size.width * factor

    fun convertHeight(factor: Factor) = size.height * factor

    fun convertMinSide(factor: Factor) = size.minSize() * factor

    fun getCorners(): List<Point2d> {
        return listOf(
            getPoint(START, START),
            getPoint(END, START),
            getPoint(END, END),
            getPoint(START, END),
        )
    }

    fun getPolygon() = Polygon2d(getCorners())

    fun getPoint(horizontal: Factor, vertical: Factor) = Point2d(
        start.x + size.width * horizontal,
        start.y + size.height * vertical,
    )

    fun getMirroredPointsOverX(x: Factor, height: Factor): Pair<Point2d, Point2d> {
        return Pair(
            getPoint(x, getStartX(height)),
            getPoint(x, getEndX(height))
        )
    }

    fun getMirroredPoints(width: Factor, y: Factor): Pair<Point2d, Point2d> {
        return Pair(
            getPoint(getStartX(width), y),
            getPoint(getEndX(width), y)
        )
    }

    fun mirrorHorizontally(polygon: Polygon2d): Polygon2d {
        val mirrorY = start.y + size.height / 2.0f

        return Polygon2d(polygon.corners.map { Point2d(it.x, mirrorY * 2.0f - it.y) })
    }

    fun mirrorVertically(polygon: Polygon2d): Polygon2d {
        val mirrorX = start.x + size.width / 2.0f

        return Polygon2d(polygon.corners.map { Point2d(mirrorX * 2.0f - it.x, it.y) })
    }

    fun mirrorVertically(point: Point2d): Point2d {
        val mirrorX = start.x + size.width / 2.0f

        return Point2d(mirrorX * 2.0f - point.x, point.y)
    }

    operator fun plus(offset: Point2d) = AABB(start + offset, size)

    fun replaceWidth(width: Distance) = copy(size = size.replaceWidth(width))

    /**
     * Move the border outward by a certain distance.
     */
    fun grow(border: Distance) = AABB(start - border, size + border * 2.0f)

    fun growWidth(distance: Distance) = AABB(
        start.minusWidth(distance / 2),
        size.addWidth(distance),
    )

    fun growHeight(distance: Distance) = AABB(
        start.minusHeight(distance / 2),
        size.addHeight(distance),
    )

    fun growWidthByPadding(padding: Distance) = AABB(
        start.minusWidth(padding),
        size.addWidth(padding * 2),
    )

    fun growHeightByPadding(padding: Distance) = AABB(
        start.minusHeight(padding),
        size.addHeight(padding * 2),
    )

    /**
     * Grow the area around the center by a certain percentage.
     */
    fun grow(factor: Factor): AABB {
        val border = size * (factor * 0.5f)
        return AABB(start - border, size * (FULL + factor))
    }

    fun growWidth(factor: Factor) = growWidth(size.width * factor)

    fun growHeight(factor: Factor) = growHeight(size.height * factor)

    fun growWidthByPadding(factor: Factor) = growWidthByPadding(size.width * factor)

    fun growHeightByPadding(factor: Factor) = growHeightByPadding(size.height * factor)

    fun growBottom(factor: Factor): AABB {
        val distance = size.height * factor
        return AABB(start.minusHeight(distance), size.addHeight(distance))
    }

    /**
     * Move the border inward by a certain distance.
     */
    fun shrink(border: Distance) = AABB(start + border, size - border * 2.0f)

    fun shrinkWidth(distance: Distance) = AABB(
        start.addWidth(distance / 2),
        size.minusWidth(distance),
    )

    fun shrinkWidthByPadding(padding: Distance) = AABB(
        start.addWidth(padding),
        size.minusWidth(padding * 2),
    )

    fun shrinkHeight(distance: Distance) = AABB(
        start.addHeight(distance / 2),
        size.minusHeight(distance),
    )

    fun shrinkHeightByPadding(padding: Distance) = AABB(
        start.addHeight(padding),
        size.minusHeight(padding * 2),
    )

    /**
     * Shrink the area around the center by a certain percentage.
     */
    fun shrink(factor: Factor): AABB {
        val border = size * (factor * 0.5f)
        return AABB(start + border, size * (FULL - factor))
    }

    fun shrinkWidth(factor: Factor) = shrinkWidth(size.width * (FULL - factor))

    fun shrinkWidthByPadding(factor: Factor) = shrinkWidthByPadding(size.width * factor)

    fun shrinkHeight(factor: Factor) = shrinkHeight(size.height * (FULL - factor))

    fun shrinkHeightByPadding(factor: Factor) = shrinkHeightByPadding(size.height * factor)

    fun splitHorizontal(start: Factor, end: Factor): AABB {
        val startPoint = getPoint(start, START)
        val endPoint = getPoint(end, END)

        return fromCorners(startPoint, endPoint)
    }

    fun createSubAabb(horizontal: Factor, vertical: Factor, width: Factor, height: Factor) =
        fromWidthAndHeight(getPoint(horizontal, vertical), convertWidth(width), convertHeight(height))
}

/**
 * Returns the start x coordinated, if the width is centered.
 */
fun getStartX(width: Factor) = fromNumber(0.5f - width.toNumber() / 2.0f)

/**
 * Returns the start x coordinated, if the width is centered.
 */
fun getEndX(width: Factor) = fromNumber(0.5f + width.toNumber() / 2.0f)
