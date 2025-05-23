package at.orchaldir.gm.utils.math.shape

import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.FULL_CIRCLE
import kotlin.math.pow

enum class CircularShape {
    Circle,
    Teardrop,
    ReverseTeardrop,
    Triangle,
    CutoffTriangle,
    RoundedTriangle,
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
        Teardrop, ReverseTeardrop, RoundedTriangle, RoundedSquare, RoundedDiamond, ScallopedOctagon, ScallopedDodecagonal -> true
        else -> false
    }

    fun isScalloped() = this == ScallopedOctagon || this == ScallopedDodecagonal

    fun calculateArea(radius: Distance) =
        Math.PI.toFloat() * radius.toMeters().pow(2)

    fun calculateIncircle(radius: Distance, inner: CircularShape): Distance {
        val sides = getSides()

        if ((this == Teardrop && inner != Teardrop) || (this == ReverseTeardrop && inner != ReverseTeardrop)) {
            return radius / 2
        } else if (this == Circle ||
            (sides == inner.getSides() && hasCornerAtTop() == inner.hasCornerAtTop())
        ) {
            return radius
        }

        require(sides >= 3) { "Requires at least 3 sides!" }

        val angle = FULL_CIRCLE.div(sides * 2.0f)
        val incircleRadius = radius * angle.cos()

        if (isScalloped()) {
            return incircleRadius * 0.8f
        }

        return incircleRadius
    }

    fun calculateVolume(radius: Distance, thickness: Distance) =
        calculateArea(radius) * thickness.toMeters()

    fun getSides() = when (this) {
        Circle, Teardrop, ReverseTeardrop -> 0
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