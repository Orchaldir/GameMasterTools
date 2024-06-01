package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Polygon2d(val corners: List<Point2d>) {

    init {
        require(corners.size > 2) { "The polygon has less than 3 corners!" }
    }
}
