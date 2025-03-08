package at.orchaldir.gm.utils.math

sealed class SegmentWeightCalculator {

    abstract fun calculate(index: Int): Float

}

class ConstantWeight private constructor(
    private val segments: Int,
    private val weight: Float,
) : SegmentWeightCalculator() {

    constructor(segments: Int) : this(segments, 1.0f / segments)

    override fun calculate(index: Int): Float {
        require(index in 0..<segments) { "Index $index is invalid!" }

        return weight
    }
}

class LinearDecreasingWeight private constructor(
    private val segments: Int,
    private val totalWeight: Float,
) : SegmentWeightCalculator() {

    constructor(segments: Int) : this(segments, (0..<segments).sumOf { it + 2 }.toFloat())

    override fun calculate(index: Int): Float {
        require(index in 0..<segments) { "Index $index is invalid!" }

        return (segments - index + 1) / totalWeight
    }
}

