package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

val START = Factor(0.0f)
val CENTER = Factor(0.5f)
val END = Factor(1.0f)

/**
 * A distance relative to the parent AABB.
 */
@Serializable
data class Factor(val value: Float) {

    operator fun plus(other: Factor) = Factor(value + other.value)
    operator fun minus(other: Factor) = Factor(value - other.value)
    operator fun times(other: Float) = Factor(value * other)

}
