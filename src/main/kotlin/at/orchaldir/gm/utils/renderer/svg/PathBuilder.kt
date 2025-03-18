package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.visualization.toInt

class PathBuilder(private val parts: MutableList<String> = mutableListOf()) {

    fun moveTo(point: Point2d) = moveTo(point.x, point.y)

    fun moveTo(x: Float, y: Float): PathBuilder {
        val part = String.format(LOCALE, "M %.3f %.3f", x, y)
        return add(part)
    }

    fun lineTo(point: Point2d) = lineTo(point.x, point.y)

    fun lineTo(x: Float, y: Float): PathBuilder {
        val part = String.format(LOCALE, "L %.3f %.3f", x, y)
        return add(part)
    }

    fun curveTo(control: Point2d, end: Point2d): PathBuilder {
        val part = String.format(LOCALE, "Q %.3f %.3f %.3f %.3f", control.x, control.y, end.x, end.y)
        return add(part)
    }

    fun ellipticalArc(
        endX: Float,
        endY: Float,
        radiusX: Float,
        radiusY: Float,
        xAxisRotation: Float = 0.0f,
        largeArcFlag: Boolean = false,
        sweepFlag: Boolean = false,
    ): PathBuilder {
        val part = String.format(
            LOCALE,
            "A %.3f %.3f %.3f %d %d %.3f %.3f",
            radiusX,
            radiusY,
            xAxisRotation,
            largeArcFlag.toInt(),
            sweepFlag.toInt(),
            endX,
            endY,
        )
        return add(part)
    }

    fun close() = add("Z")

    private fun add(part: String): PathBuilder {
        parts.add(part)

        return this
    }

    fun build() = parts.joinToString(" ")

}