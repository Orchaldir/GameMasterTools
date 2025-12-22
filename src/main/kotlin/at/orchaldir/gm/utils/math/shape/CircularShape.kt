package at.orchaldir.gm.utils.math.shape

import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.FULL_CIRCLE
import at.orchaldir.gm.utils.math.unit.Volume
import kotlin.math.pow

enum class CircularShape {
    Circle,
    Triangle,
    CutoffTriangle,
    RoundedTriangle,
    Heater,
    RoundedHeater,
    Square,
    CutoffSquare,
    RoundedSquare,
    Diamond,
    CutoffDiamond,
    RoundedDiamond,
    Pentagon,
    Hexagon,
    Heptagon,
    Octagon,
    ScallopedOctagon,
    Dodecagonal,
    ScallopedDodecagonal;

    fun isRounded() = when (this) {
        RoundedTriangle, Heater, RoundedHeater, RoundedSquare, RoundedDiamond, ScallopedOctagon, ScallopedDodecagonal -> true
        else -> false
    }

    fun isScalloped() = this == ScallopedOctagon || this == ScallopedDodecagonal

    fun calculateArea(radius: Distance) =
        Math.PI.toFloat() * radius.toMeters().pow(2)

    fun calculateVolumeOfPrism(radius: Distance, thickness: Distance) =
        Volume.fromCubicMeters(calculateArea(radius) * thickness.toMeters())

    fun calculateIncircle(radius: Distance, inner: CircularShape): Distance {
        val sides = getSides()

        if (this == Circle ||
            this == Heater ||
            this == RoundedHeater ||
            (sides == inner.getSides() && hasCornerAtTop() == inner.hasCornerAtTop())
        ) {
            return radius
        }

        require(sides >= 3) { "Requires at least 3 sides!" }

        val angle = FULL_CIRCLE.div(sides * 2.0f)
        val incircleRadius = radius * angle.cos()

        if (isScalloped()) {
            return incircleRadius * 0.9f
        }

        return incircleRadius
    }

    fun getSides() = when (this) {
        Heater, RoundedHeater, Circle -> 0
        Triangle, CutoffTriangle, RoundedTriangle -> 3
        Square, CutoffSquare, RoundedSquare, Diamond, CutoffDiamond, RoundedDiamond -> 4
        Pentagon -> 5
        Hexagon -> 6
        Heptagon -> 7
        Octagon, ScallopedOctagon -> 8
        Dodecagonal, ScallopedDodecagonal -> 12
    }

    fun hasCornerAtTop() = !(this == Square || this == RoundedSquare)
}