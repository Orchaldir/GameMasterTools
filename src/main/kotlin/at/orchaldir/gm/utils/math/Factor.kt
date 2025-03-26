package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import kotlinx.serialization.Serializable

val START = fromPercentage(0)
val ZERO = fromPercentage(0)
val CENTER = fromPercentage(50)
val HALF = fromPercentage(50)
val END = fromPercentage(100)
val FULL = fromPercentage(100)

/**
 * A distance relative to the parent AABB.
 */
@JvmInline
@Serializable
value class Factor private constructor(val value: Float) {

    companion object {
        fun fromNumber(number: Float) = Factor(number)
        fun fromPercentage(percentage: Int) = Factor(percentage / 100.0f)
        fun fromPermille(permille: Int) = Factor(permille / 1000.0f)
    }

    operator fun unaryMinus() = Factor(-value)
    operator fun plus(other: Factor) = Factor(value + other.value)
    operator fun minus(other: Factor) = Factor(value - other.value)
    operator fun times(other: Factor) = Factor(value * other.value)
    operator fun times(other: Float) = Factor(value * other)
    operator fun div(other: Factor) = Factor(value / other.value)
    operator fun div(other: Float) = Factor(value / other)
    operator fun div(factor: Int) = Factor(value / factor)

    fun interpolate(other: Factor, between: Factor) =
        Factor(value * (1.0f - between.value) + other.value * between.value)

}
