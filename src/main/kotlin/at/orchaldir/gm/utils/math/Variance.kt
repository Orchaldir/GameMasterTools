package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.NumberGenerator
import kotlinx.serialization.Serializable

@Serializable
data class Variance<T: Value<T>>(
    val center: T,
    val offset: T,
) {
    constructor(center: T): this(center, center.zero())

    fun generate(numberGenerator: NumberGenerator) = center + offset * numberGenerator.getFloat(-1.0f, 1.0f)

    fun display() = String.format("%s +- %s", center, offset)

    fun getMin() = center - offset
    fun getMax() = center + offset

    fun isInside(value: T) = value >= getMin() &&  value <= getMax()
}