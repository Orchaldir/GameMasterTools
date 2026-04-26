package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Variance<T: Value<T>>(
    val center: T,
    val offset: T,
) {
    constructor(center: T): this(center, center.zero())

    fun display() = String.format("%s +- %s", center, offset)

    fun getMin() = center - offset
    fun getMax() = center + offset

    fun isInside(value: T) = value >= getMin() &&  value <= getMax()
}