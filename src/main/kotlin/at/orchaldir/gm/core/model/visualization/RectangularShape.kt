package at.orchaldir.gm.core.model.visualization

enum class RectangularShape {
    Ellipse,
    Rectangle,
    RoundedRectangle;

    fun isRounded() = when (this) {
        RoundedRectangle, Ellipse -> true
        else -> false
    }
}