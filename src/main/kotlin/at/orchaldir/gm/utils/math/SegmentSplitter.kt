package at.orchaldir.gm.utils.math

class SegmentSplitter private constructor(
    private val start: Point2d,
    private val diff: Point2d,
    private val weightCalculator: SegmentWeightCalculator,
) {

    companion object {
        fun fromStartAndEnd(start: Point2d, end: Point2d, weightCalculator: SegmentWeightCalculator) =
            SegmentSplitter(start, end - start, weightCalculator)

        fun fromStartAndEnd(start: Point2d, end: Point2d, segments: Int) =
            SegmentSplitter(start, end - start, ConstantWeight(segments))

        fun fromStartAndEnd(pair: Pair<Point2d, Point2d>, weightCalculator: SegmentWeightCalculator) =
            fromStartAndEnd(pair.first, pair.second, weightCalculator)

        fun fromStartAndEnd(pair: Pair<Point2d, Point2d>, segments: Int) =
            fromStartAndEnd(pair.first, pair.second, segments)
    }

    fun getCenter(index: Int): Point2d {
        val weightUntil = weightCalculator.sumUntil(index)
        val weightOfSegment = weightCalculator.calculate(index)
        val weightOfCenter = weightUntil + weightOfSegment / 2.0f

        return start + diff * weightOfCenter
    }

    fun getCenters(): List<Point2d> {
        val points = mutableListOf<Point2d>()
        var startOfSegment = start

        repeat(weightCalculator.segments()) {
            val step = diff * weightCalculator.calculate(it)
            val center = startOfSegment + step / 2.0f

            points.add(center)

            startOfSegment += step
        }

        return points
    }

    fun getCorners(): List<Point2d> {
        val points = mutableListOf(start)
        var point = start

        repeat(weightCalculator.segments()) {
            val step = diff * weightCalculator.calculate(it)
            point += step

            points.add(point)
        }

        return points
    }
}