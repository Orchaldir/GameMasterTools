package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.visualization.toInt

class PathBuilder(private val parts: MutableList<String> = mutableListOf()) {

    fun moveTo(point: Point2d) = moveTo(point.x, point.y)

    fun moveTo(x: Float, y: Float): PathBuilder {
        val part = String.format(LOCALE, "M %.3f %.3f", x, y)
        parts.add(part)

        return this
    }

    fun ellipticalArc(
        x: Float,
        y: Float,
        radiusX: Float,
        radiusY: Float,
        xAxisRotation: Float = 0.0f,
        largeArcFlag: Boolean = false,
        sweepFlag: Boolean = false,
    ): PathBuilder {
        val part = String.format(
            LOCALE,
            "A %.3f %.3f %.3f %d %d %.3f %.3f",
            x,
            y,
            xAxisRotation,
            largeArcFlag.toInt(),
            sweepFlag.toInt(),
            radiusX,
            radiusY,
        )
        parts.add(part)

        return this
    }

    fun close(): PathBuilder {
        parts.add("Z")

        return this
    }

    fun build() = parts.joinToString(" ")

}