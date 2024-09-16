package at.orchaldir.gm.utils.map

import kotlinx.serialization.Serializable

@Serializable
data class MapSize2d(val width: Int, val height: Int) {

    init {
        require(width >= 0) { "Width muster be greater or equal 0!" }
        require(height >= 0) { "Height muster be greater or equal 0!" }
    }

    companion object {
        fun square(size: Int) = MapSize2d(size, size)
    }

    fun tiles() = width * height

    fun isInside(x: Int, y: Int) = x in 0..<width && y in 0..<height

    fun toIndex(x: Int, y: Int): Int? {
        if (isInside(x, y)) {
            return toIndexRisky(x, y)
        }

        return null
    }

    fun toIndexRisky(x: Int, y: Int) = y * width + x

}
