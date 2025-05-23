package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMicrometers
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.math.absoluteValue

val ZERO_DISTANCE = fromMillimeters(0)
val HUNDRED_µM = fromMicrometers(100)
val ONE_MM = fromMillimeters(1)
val ONE_CM = fromCentimeters(1)
val ONE_DM = fromCentimeters(10)
val ONE_M = fromMeters(1)

@JvmInline
@Serializable
value class Distance private constructor(private val micrometers: Long) : SiUnit<Distance> {

    companion object {
        fun fromKilometers(kilometers: Long) = Distance(convertFromKilometers(kilometers))
        fun fromMeters(meters: Long) = Distance(convertFromMeters(meters))
        fun fromMeters(meters: Float) = Distance(convertFromMeters(meters))
        fun fromCentimeters(centimeter: Long) = Distance(convertFromCentimeters(centimeter))
        fun fromMillimeters(millimeter: Long) = Distance(convertFromMillimeters(millimeter))
        fun fromMillimeters(millimeter: Float) = Distance(convertFromMillimeters(millimeter))
        fun fromMicrometers(micrometers: Long) = Distance(micrometers)

        fun from(prefix: SiPrefix, value: Long) = when (prefix) {
            SiPrefix.Kilo -> fromKilometers(value)
            SiPrefix.Base -> fromMeters(value)
            SiPrefix.Centi -> fromCentimeters(value)
            SiPrefix.Milli -> fromMillimeters(value)
            SiPrefix.Micro -> fromMicrometers(value)
        }

        fun resolveUnit(prefix: SiPrefix) = prefix.resolveUnit() + "m"
    }

    override fun value() = micrometers
    override fun convertToLong(prefix: SiPrefix) = when (prefix) {
        SiPrefix.Kilo -> convertToKilometers(micrometers).toLong()
        SiPrefix.Base -> convertToMeters(micrometers).toLong()
        SiPrefix.Centi -> convertToCentimeters(micrometers).toLong()
        SiPrefix.Milli -> convertToMillimeters(micrometers).toLong()
        SiPrefix.Micro -> micrometers
    }

    fun toMeters() = convertToMeters(micrometers)
    fun toMillimeters() = convertToMillimeters(micrometers)
    fun toMicrometers() = micrometers

    override fun toString() = formatMicrometersAsMeters(micrometers)

    operator fun unaryMinus() = Distance(-micrometers)
    override operator fun plus(other: Distance) = Distance(micrometers + other.micrometers)
    override operator fun minus(other: Distance) = Distance(micrometers - other.micrometers)
    operator fun times(factor: Float) = Distance((micrometers * factor).toLong())
    override operator fun times(factor: Factor) = times(factor.toNumber())
    operator fun times(factor: Int) = Distance(micrometers * factor)
    operator fun div(factor: Float) = Distance((micrometers / factor).toLong())
    operator fun div(factor: Factor) = div(factor.toNumber())
    operator fun div(factor: Int) = Distance(micrometers / factor)
    operator fun div(other: Distance) = Factor.fromNumber(micrometers / other.micrometers.toFloat())

    operator fun compareTo(other: Distance): Int = micrometers.compareTo(other.micrometers)

    fun isZero() = micrometers == 0L

    fun max(other: Distance) = if (micrometers >= other.micrometers) {
        this
    } else {
        other
    }

    fun min(other: Distance) = if (micrometers <= other.micrometers) {
        this
    } else {
        other
    }
}

fun checkDistance(distance: Distance, label: String, min: Distance, max: Distance) {
    require(distance >= min) { "The $label is too small!" }
    require(distance <= max) { "The $label is too large!" }
}

// to lower

fun convertFromKilometers(kilometer: Long) = downNineSteps(kilometer)

fun convertFromMeters(meter: Long) = downSixSteps(meter)
fun convertFromMeters(meter: Float) = downSixSteps(meter)

fun convertFromCentimeters(centimeter: Long) = downThreeSteps(centimeter * 10)
fun convertFromCentimeters(centimeter: Float) = downThreeSteps(centimeter * 10.0f)

fun convertFromMillimeters(millimeter: Long) = downThreeSteps(millimeter)
fun convertFromMillimeters(millimeter: Float) = downThreeSteps(millimeter)

// to higher

fun convertToKilometers(micrometers: Long) = upNineSteps(micrometers)
fun convertToMeters(micrometers: Long) = upSixSteps(micrometers)
fun convertToCentimeters(millimeters: Long) = up(upThreeSteps(millimeters))
fun convertToMillimeters(micrometers: Long) = upThreeSteps(micrometers)

fun formatMicrometersAsMeters(micrometers: Long): String {
    val abs = micrometers.absoluteValue

    return if (abs > SI_SIX_STEPS) {
        String.format(Locale.US, "%.2f m", convertToMeters(micrometers))
    } else if (abs > SI_FOUR_STEPS) {
        String.format(Locale.US, "%.2f cm", convertToCentimeters(micrometers))
    } else if (abs > SI_THREE_STEPS) {
        String.format(Locale.US, "%.2f mm", convertToMillimeters(micrometers))
    } else {
        String.format(Locale.US, "%d μm", micrometers)
    }
}


fun maxOf(distances: Collection<Distance>) = distances.maxBy { it.value() }

fun sumOf(distances: Collection<Distance>) = distances.fold(ZERO_DISTANCE) { sum, distance ->
    sum + distance
}
