package at.orchaldir.gm.utils.math

import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.Factor.Companion.fromPermille
import kotlinx.serialization.Serializable

val START = fromPercentage(0)
val ZERO = fromPercentage(0)
val ONE_TENTH_PERCENT = fromPermille(1)
val ONE_PERCENT = fromPercentage(1)
val QUARTER = fromPercentage(25)
val THIRD = fromPercentage(33)
val CENTER = fromPercentage(50)
val HALF = fromPercentage(50)
val TWO_THIRD = fromPercentage(66)
val THREE_QUARTER = fromPercentage(75)
val END = fromPercentage(100)
val FULL = fromPercentage(100)
val ONE = fromPercentage(100)
val DOUBLE = fromPercentage(200)

private const val NUMBER_FACTOR = 10000
private const val PERCENTAGE_FACTOR = 100
private const val PERMILLE_FACTOR = 10

/**
 * A number stored as permyriad (1 in 10k), but mostly used to represent percentage.
 */
@JvmInline
@Serializable
value class Factor private constructor(private val permyriad: Int) {

    companion object {
        fun fromNumber(number: Float) = Factor((number * NUMBER_FACTOR).toInt())
        fun fromNumber(number: Int) = Factor(number * NUMBER_FACTOR)
        fun fromPercentage(percentage: Int) = Factor(percentage * PERCENTAGE_FACTOR)
        fun fromPermille(permille: Int) = Factor(permille * PERMILLE_FACTOR)
        fun fromPermyriad(permyriad: Int) = Factor(permyriad)
        fun divideTwoInts(dividend: Int, divisor: Int) = fromNumber(dividend / divisor.toFloat())
    }

    fun isGreaterZero() = permyriad > 0
    fun isLessOrEqualOne() = permyriad <= NUMBER_FACTOR

    fun requireGreaterZero(text: String) = require(isGreaterZero()) { text }

    fun toNumber() = permyriad / NUMBER_FACTOR.toFloat()
    fun toPercentage() = permyriad / PERCENTAGE_FACTOR.toFloat()
    fun toPermyriad() = permyriad

    fun apply(value: Int) = (value * toNumber()).toInt()

    override fun toString() = formatAsFactor(permyriad)

    operator fun unaryMinus() = Factor(-permyriad)
    operator fun plus(other: Factor) = Factor(permyriad + other.permyriad)
    operator fun minus(other: Factor) = Factor(permyriad - other.permyriad)
    operator fun times(other: Factor) = fromNumber(toNumber() * other.toNumber())
    operator fun times(other: Float) = Factor((permyriad * other).toInt())
    operator fun times(other: Int) = Factor(permyriad * other)
    operator fun div(other: Factor) = fromNumber(toNumber() / other.toNumber())
    operator fun div(other: Float) = Factor((permyriad / other).toInt())
    operator fun div(factor: Int) = Factor(permyriad / factor)

    operator fun compareTo(other: Factor): Int = permyriad.compareTo(other.permyriad)

    fun max(other: Factor) = if (permyriad >= other.permyriad) {
        this
    } else {
        other
    }

    fun min(other: Factor) = if (permyriad <= other.permyriad) {
        this
    } else {
        other
    }

    fun interpolate(other: Factor, between: Factor) = this * (FULL - between) + other * between

}

fun checkFactor(
    factor: Factor,
    label: String,
    min: Factor,
    max: Factor,
) {
    require(factor >= min) { "The $label factor is too small!" }
    require(factor <= max) { "The $label factor is too large!" }
}

fun percentageOnly(permyriad: Int) = permyriad / PERCENTAGE_FACTOR
fun permilleOnly(permyriad: Int) = (permyriad % PERCENTAGE_FACTOR) / PERMILLE_FACTOR

fun formatAsFactor(permyriad: Int): String {
    val percentageOnly = percentageOnly(permyriad)
    val permilleOnly = permilleOnly(permyriad)

    return if (permilleOnly == 0) {
        "$percentageOnly%"
    } else {
        String.format("%d.%01d%%", percentageOnly, permilleOnly)
    }
}

fun maxOf(distances: Collection<Factor>) = distances.maxBy { it.toPermyriad() }

fun sumOf(distances: Collection<Factor>) = distances.fold(ZERO) { sum, distance ->
    sum + distance
}
