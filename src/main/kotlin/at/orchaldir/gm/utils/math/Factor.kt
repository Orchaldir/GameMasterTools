package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

/**
 * A distance relative to the parent AABB.
 */
@Serializable
data class Factor(val value: Float) {

    operator fun plus(other: Factor) = Factor(value + other.value)
    operator fun times(other: Float) = Factor(value * other)

}
