package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.utils.math.FULL_CIRCLE
import at.orchaldir.gm.utils.math.unit.Distance

enum class Shape {
    Circle,
    Triangle,
    RoundedTriangle,
    Square,
    RoundedSquare,
    Diamond,
    Pentagon,
    Hexagon,
    Heptagon,
    Octagon,
    Dodecagonal;

    fun isRounded() = this == RoundedTriangle || this == RoundedSquare

    fun getSides() = when (this) {
        Circle -> 0
        Triangle, RoundedTriangle -> 3
        Square, RoundedSquare, Diamond -> 4
        Pentagon -> 5
        Hexagon -> 6
        Heptagon -> 7
        Octagon -> 8
        Dodecagonal -> 12
    }

    fun hasCornerAtTop() = !(this == Square || this == RoundedSquare)
}

fun calculateIncircle(radius: Distance, sides: Int): Distance {
    require(sides >= 3) { "Requires at least 3 sides!" }
    val angle = FULL_CIRCLE.div(sides * 2.0f)

    return radius * angle.cos()
}