package at.orchaldir.gm.utils.map

import at.orchaldir.gm.utils.math.modulo
import kotlinx.serialization.Serializable

@Serializable
data class MapSize2d(val width: Int, val height: Int) {

    init {
        // TODO: can be > 0 again?
        require(width >= 0) { "Width must be greater or equal 0!" }
        require(height >= 0) { "Height must be greater or equal 0!" }
    }

    companion object {
        fun square(size: Int) = MapSize2d(size, size)
    }

    fun tiles() = width * height

    fun apply(resize: Resize) =
        MapSize2d(width + resize.widthStart + resize.widthEnd, height + resize.heightStart + resize.heightEnd)

    fun limit(limitWidth: Int, limitHeight: Int) =
        MapSize2d(width.coerceAtMost(limitWidth), height.coerceAtMost(limitHeight))

    fun limit(x: Int, y: Int, limits: MapSize2d) =
        limit(limits.width - x, limits.height - y)

    fun isInside(index: Int) = index in 0..<tiles()

    fun isInside(x: Int, y: Int) = isXInside(x) && isYInside(y)

    fun isXInside(x: Int) = x in 0..<width

    fun isYInside(y: Int) = y in 0..<height

    fun toIndices(index: Int, size: MapSize2d): List<Int>? {
        if (!isInside(index)) {
            return null
        }

        val startX = toX(index)
        val startY = toY(index)
        val indices = mutableListOf<Int>()

        for (y in startY..<(startY + size.height)) {
            for (x in startX..<(startX + size.width)) {
                val tileIndex = toIndex(x, y) ?: return null

                indices.add(tileIndex)
            }
        }

        return indices
    }

    fun toIndex(x: Int, y: Int): Int? {
        if (isInside(x, y)) {
            return toIndexRisky(x, y)
        }

        return null
    }

    fun toIndexRisky(x: Int, y: Int) = y * width + x

    fun toX(index: Int) = index.modulo(width)

    fun toY(index: Int) = index / width

    fun format() = "$width x $height"

    operator fun plus(other: MapSize2d) = MapSize2d(width + other.width, height + other.height)
    operator fun minus(other: MapSize2d) = MapSize2d(width - other.width, height - other.height)

    operator fun plus(point: MapPoint2d) = MapSize2d(width + point.x, height + point.y)
    operator fun minus(point: MapPoint2d) = MapSize2d(width - point.x, height - point.y)

}
