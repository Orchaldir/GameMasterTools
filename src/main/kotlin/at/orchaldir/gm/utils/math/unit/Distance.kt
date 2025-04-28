package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMicrometers
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import kotlinx.serialization.Serializable
import java.util.*

private const val FACTOR = 1000
private const val SQUARED = FACTOR * FACTOR

val ZERO = fromMillimeters(0)
val HUNDRED_µM = fromMicrometers(100)
val ONE_MM = fromMillimeters(1)
val ONE_CM = fromCentimeters(1)
val ONE_DM = fromCentimeters(10)
val ONE_M = fromMeters(1)

@JvmInline
@Serializable
value class Distance private constructor(private val micrometers: Int) : SiUnit<Distance> {

    init {
        require(micrometers >= 0) { "Distance must be >= 0 μm!" }
    }

    companion object {
        fun fromMeters(meters: Int) = Distance(meterToMicrometers(meters))
        fun fromMeters(meters: Float) = Distance(meterToMicrometers(meters))
        fun fromCentimeters(centimeter: Int) = Distance(centimeterToMicrometers(centimeter))
        fun fromMillimeters(millimeter: Int) = Distance(millimeterToMicrometers(millimeter))
        fun fromMillimeters(millimeter: Float) = Distance(millimeterToMicrometers(millimeter))
        fun fromMicrometers(micrometers: Int) = Distance(micrometers)
    }

    override fun value() = micrometers

    fun toMeters() = micrometersToMeter(micrometers)
    fun toMillimeters() = micrometersToMillimeter(micrometers)
    fun toMicrometers() = micrometers

    override fun toString() = formatMicrometersAsMeters(micrometers)

    override operator fun plus(other: Distance) = Distance(micrometers + other.micrometers)
    override operator fun minus(other: Distance) = Distance(micrometers - other.micrometers)
    operator fun times(factor: Float) = Distance((micrometers * factor).toInt())
    operator fun times(factor: Factor) = times(factor.toNumber())
    operator fun times(factor: Int) = Distance(micrometers * factor)
    operator fun div(factor: Float) = Distance((micrometers / factor).toInt())
    operator fun div(factor: Int) = Distance(micrometers / factor)

    operator fun compareTo(other: Distance): Int = micrometers.compareTo(other.micrometers)

    fun max(other: Distance) = if (micrometers >= other.micrometers) {
        this
    } else {
        other
    }
}

fun metersOnly(micrometers: Int) = micrometers / FACTOR
fun millimetersOnly(micrometers: Int) = micrometers % FACTOR

// to lower
private fun down(value: Int) = value * FACTOR
private fun down(value: Float) = (value * FACTOR).toInt()

fun meterToMillimeter(meter: Int) = down(meter)
fun meterToMillimeter(meter: Float) = down(meter)

fun meterToMicrometers(meter: Int) = down(down(meter))
fun meterToMicrometers(meter: Float) = down(down(meter))

fun centimeterToMicrometers(centimeter: Int) = down(centimeter * 10)
fun centimeterToMicrometers(centimeter: Float) = down(centimeter * 10.0f)

fun millimeterToMicrometers(millimeter: Int) = down(millimeter)
fun millimeterToMicrometers(millimeter: Float) = down(millimeter)

// to higher
private fun up(value: Int) = value / FACTOR.toFloat()
private fun up(value: Float) = value / FACTOR.toFloat()

fun millimeterToMeter(millimeter: Int) = up(millimeter)
fun micrometersToMeter(micrometers: Int) = up(up(micrometers))
fun micrometersToMillimeter(micrometers: Int) = up(micrometers)

fun formatMicrometersAsMeters(micrometers: Int) = if (micrometers > SQUARED) {
    String.format(Locale.US, "%.2f m", micrometersToMeter(micrometers))
} else if (micrometers > FACTOR) {
    String.format(Locale.US, "%.2f mm", micrometersToMillimeter(micrometers))
} else {
    String.format(Locale.US, "%d μm", micrometers)
}


fun maxOf(distances: Collection<Distance>) = distances.maxBy { it.value() }

fun sumOf(distances: Collection<Distance>) = distances.fold(ZERO) { sum, distance ->
    sum + distance
}
