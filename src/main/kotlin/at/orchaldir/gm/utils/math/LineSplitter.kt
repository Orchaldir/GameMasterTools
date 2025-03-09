package at.orchaldir.gm.utils.math

class LineSplitter private constructor(
    private val start: Point2d,
    private val diff: Point2d,
    private val weightCalculator: SegmentWeightCalculator,
) {

    companion object {
        fun fromStartAndEnd(start: Point2d, end: Point2d, weightCalculator: SegmentWeightCalculator) =
            LineSplitter(start, end - start, weightCalculator)
    }

    fun getCenter(index: Int): Point2d {
        val weightUntil = weightCalculator.sumUntil(index)
        val weightOfSegment = weightCalculator.calculate(index)
        val weightOfCenter = weightUntil + weightOfSegment / 2.0f

        return start + diff * weightOfCenter
    }
}