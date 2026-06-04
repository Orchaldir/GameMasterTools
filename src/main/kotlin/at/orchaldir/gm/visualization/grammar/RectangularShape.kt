package at.orchaldir.gm.visualization.grammar

enum class RectangularShape {
    Ellipse,
    Rectangle,
    RoundedRectangle;

    fun isRounded() = when (this) {
        RoundedRectangle, Ellipse -> true
        else -> false
    }
}