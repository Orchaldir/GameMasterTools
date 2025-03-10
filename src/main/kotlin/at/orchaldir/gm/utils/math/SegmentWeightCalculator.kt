package at.orchaldir.gm.utils.math

sealed class SegmentWeightCalculator {

    abstract fun segments(): Int
    abstract fun calculate(index: Int): Float
    abstract fun sumUntil(index: Int): Float

}

class ConstantWeight private constructor(
    private val segments: Int,
    private val weight: Float,
) : SegmentWeightCalculator() {

    constructor(segments: Int) : this(segments, 1.0f / segments)

    override fun segments() = segments

    override fun calculate(index: Int): Float {
        validateIndex(index, segments)

        return weight
    }

    override fun sumUntil(index: Int) = calculate(index) * index
}

class LinearDecreasingWeight private constructor(
    private val segments: Int,
    private val totalWeight: Float,
) : SegmentWeightCalculator() {

    constructor(segments: Int) : this(segments, (0..<segments).sumOf { it + 2 }.toFloat())

    override fun segments() = segments

    override fun calculate(index: Int): Float {
        validateIndex(index, segments)

        return (segments - index + 1) / totalWeight
    }

    override fun sumUntil(index: Int): Float {
        validateIndex(index, segments)

        return (0..<index)
            .map { calculate(it) }
            .sum()
    }

}

private fun validateIndex(index: Int, segments: Int) {
    require(index in 0..<segments) { "Index $index is invalid!" }
}

