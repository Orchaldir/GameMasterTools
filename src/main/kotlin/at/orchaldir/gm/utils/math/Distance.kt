package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Distance(val value: Float) {

    init {
        require(value > 0.0) { "Distance muster be greater 0!" }
    }

    operator fun plus(other: Distance) = Distance(value + other.value)
    operator fun times(other: Float) = Distance(value * other)
    operator fun times(other: Factor) = Distance(value * other.value)
    operator fun times(other: Int) = Distance(value * other)

}
