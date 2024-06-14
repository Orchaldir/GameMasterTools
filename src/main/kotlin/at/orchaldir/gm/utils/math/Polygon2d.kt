package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Polygon2d(val corners: List<Point2d>) {

    init {
        require(corners.size > 2) { "The polygon has less than 3 corners!" }
    }
}

data class Polygon2dBuilder(val corners: MutableList<Point2d>) {

    fun build() = Polygon2d(corners)

    fun createSharpCorner(index: Int): Boolean {
        val corner = corners.getOrNull(index)

        if (corner != null) {
            corners.add(index, corner)

            return true
        }

        return false
    }

}
