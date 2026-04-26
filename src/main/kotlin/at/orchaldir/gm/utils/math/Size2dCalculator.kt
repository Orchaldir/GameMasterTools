package at.orchaldir.gm.utils.math

class Size2dCalculator {
    private var min: Point2d = Point2d.square(Float.MAX_VALUE)
    private var max: Point2d = Point2d.square(Float.MIN_VALUE)

    fun process(point: Point2d) {
        min = min.min(point)
        max = max.max(point)
    }

    fun calculate() = Size2d(
        max.x - min.x,
        max.y - min.y,
    )
}
