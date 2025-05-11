package at.orchaldir.gm.utils.renderer.svg

import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.toInt

class PathBuilder(private val parts: MutableList<String> = mutableListOf()) {

    fun moveTo(point: Point2d) = moveTo(point.x, point.y)

    fun moveTo(x: Distance, y: Distance): PathBuilder {
        val part = String.format(LOCALE, "M %.4f %.4f", x.toMeters(), y.toMeters())
        return add(part)
    }

    fun lineTo(point: Point2d) = lineTo(point.x, point.y)

    fun lineTo(x: Distance, y: Distance): PathBuilder {
        val part = String.format(LOCALE, "L %.4f %.4f", x.toMeters(), y.toMeters())
        return add(part)
    }

    fun curveTo(control: Point2d, end: Point2d): PathBuilder {
        val part = String.format(LOCALE, "Q %.4f %.4f %.4f %.4f", control.x, control.y, end.x, end.y)
        return add(part)
    }

    fun ellipticalArc(
        endX: Distance,
        endY: Distance,
        radiusX: Distance,
        radiusY: Distance,
        xAxisRotation: Float = 0.0f,
        largeArcFlag: Boolean = false,
        sweepFlag: Boolean = false,
    ): PathBuilder {
        val part = String.format(
            LOCALE,
            "A %.4f %.4f %.4f %d %d %.4f %.4f",
            radiusX.toMeters(),
            radiusY.toMeters(),
            xAxisRotation,
            largeArcFlag.toInt(),
            sweepFlag.toInt(),
            endX.toMeters(),
            endY.toMeters(),
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