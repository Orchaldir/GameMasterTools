package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import kotlinx.serialization.Serializable

val START = fromPercentage(0)
val ZERO = fromPercentage(0)
val CENTER = fromPercentage(50)
val HALF = fromPercentage(50)
val END = fromPercentage(100)
val FULL = fromPercentage(100)

private const val NUMBER_FACTOR = 10000
private const val PERCENTAGE_FACTOR = 100
private const val PERMILLE_FACTOR = 10

/**
 * A number stored as permyriad (1 in 10k), but mostly used ro represent percentage.
 */
@JvmInline
@Serializable
value class Factor private constructor(private val permyriad: Int) {

    companion object {
        fun fromNumber(number: Float) = Factor((number * NUMBER_FACTOR).toInt())
        fun fromPercentage(percentage: Int) = Factor(percentage * PERCENTAGE_FACTOR)
        fun fromPermille(permille: Int) = Factor(permille * PERMILLE_FACTOR)
    }

    fun requireGreaterZero(text: String) = require(permyriad > 0) { text }

    fun toNumber() = permyriad / NUMBER_FACTOR.toFloat()
    fun toPercentage() = permyriad / PERCENTAGE_FACTOR.toFloat()
    fun toInternalValue() = toNumber()//permyriad

    operator fun unaryMinus() = Factor(-permyriad)
    operator fun plus(other: Factor) = Factor(permyriad + other.permyriad)
    operator fun minus(other: Factor) = Factor(permyriad - other.permyriad)
    operator fun times(other: Factor) = fromNumber(toNumber() * other.toNumber())
    operator fun times(other: Float) = Factor((permyriad * other).toInt())
    operator fun div(other: Factor) = fromNumber(toNumber() / other.toNumber())
    operator fun div(other: Float) = Factor((permyriad / other).toInt())
    operator fun div(factor: Int) = Factor(permyriad / factor)

    fun interpolate(other: Factor, between: Factor) = this * (FULL - between) + other * between

}
