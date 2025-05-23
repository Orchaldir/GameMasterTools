package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.FULL_CIRCLE
import at.orchaldir.gm.utils.math.unit.Weight
import kotlin.math.pow

enum class Shape {
    Circle,
    Teardrop,
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
        Teardrop, RoundedTriangle, RoundedSquare, RoundedDiamond, ScallopedOctagon, ScallopedDodecagonal -> true
        else -> false
    }

    fun isScalloped() = this == ScallopedOctagon || this == ScallopedDodecagonal

    fun calculateArea(radius: Distance) =
        Math.PI.toFloat() * radius.toMeters().pow(2)

    fun calculateIncircle(radius: Distance, inner: Shape): Distance {
        val sides = getSides()

        if (this == Teardrop && inner != Teardrop) {
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

    fun calculateWeight(radius: Distance, thickness: Distance, density: Weight) =
        Weight.fromKilograms(calculateVolume(radius, thickness) * density.toKilograms())

    fun getSides() = when (this) {
        Circle, Teardrop -> 0
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

