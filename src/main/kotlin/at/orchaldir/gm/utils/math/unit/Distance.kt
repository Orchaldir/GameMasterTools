package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromCentimeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMeters
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMicrometers
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import kotlinx.serialization.Serializable
import java.util.*

val ZERO = fromMillimeters(0)
val HUNDRED_µM = fromMicrometers(100)
val ONE_MM = fromMillimeters(1)
val ONE_CM = fromCentimeters(1)
val ONE_DM = fromCentimeters(10)
val ONE_M = fromMeters(1)

@JvmInline
@Serializable
value class Distance private constructor(private val micrometers: Long) : SiUnit<Distance> {

    init {
        require(micrometers >= 0) { "Distance must be >= 0 μm!" }
    }

    companion object {
        fun fromMeters(meters: Long) = Distance(meterToMicrometers(meters))
        fun fromMeters(meters: Float) = Distance(meterToMicrometers(meters))
        fun fromCentimeters(centimeter: Long) = Distance(centimeterToMicrometers(centimeter))
        fun fromMillimeters(millimeter: Long) = Distance(millimeterToMicrometers(millimeter))
        fun fromMillimeters(millimeter: Float) = Distance(millimeterToMicrometers(millimeter))
        fun fromMicrometers(micrometers: Long) = Distance(micrometers)
    }

    override fun value() = micrometers
    override fun convertTo(prefix: SiPrefix) = when (prefix) {
        SiPrefix.Kilo -> upNineSteps(micrometers).toLong()
        SiPrefix.Base -> upSixSteps(micrometers).toLong()
        SiPrefix.Milli -> upThreeSteps(micrometers).toLong()
        SiPrefix.Micro -> micrometers
    }

    fun toMeters() = toMeters(micrometers)
    fun toMillimeters() = toMillimeters(micrometers)
    fun toMicrometers() = micrometers

    override fun toString() = formatMicrometersAsMeters(micrometers)

    override operator fun plus(other: Distance) = Distance(micrometers + other.micrometers)
    override operator fun minus(other: Distance) = Distance(micrometers - other.micrometers)
    operator fun times(factor: Float) = Distance((micrometers * factor).toLong())
    operator fun times(factor: Factor) = times(factor.toNumber())
    operator fun times(factor: Int) = Distance(micrometers * factor)
    operator fun div(factor: Float) = Distance((micrometers / factor).toLong())
    operator fun div(factor: Int) = Distance(micrometers / factor)

    operator fun compareTo(other: Distance): Int = micrometers.compareTo(other.micrometers)

    fun max(other: Distance) = if (micrometers >= other.micrometers) {
        this
    } else {
        other
    }
}

// to lower

fun meterToMillimeter(meter: Long) = downThreeSteps(meter)
fun meterToMillimeter(meter: Float) = downThreeSteps(meter)

fun meterToMicrometers(meter: Long) = downSixSteps(meter)
fun meterToMicrometers(meter: Float) = downSixSteps(meter)

fun centimeterToMicrometers(centimeter: Long) = downThreeSteps(centimeter * 10)
fun centimeterToMicrometers(centimeter: Float) = downThreeSteps(centimeter * 10.0f)

fun millimeterToMicrometers(millimeter: Long) = downThreeSteps(millimeter)
fun millimeterToMicrometers(millimeter: Float) = downThreeSteps(millimeter)

// to higher

fun toMeters(micrometers: Long) = upSixSteps(micrometers)
fun toMillimeters(micrometers: Long) = upThreeSteps(micrometers)

fun formatMicrometersAsMeters(micrometers: Long) = if (micrometers > SI_SQUARED) {
    String.format(Locale.US, "%.2f m", toMeters(micrometers))
} else if (micrometers > SI_FACTOR) {
    String.format(Locale.US, "%.2f mm", toMillimeters(micrometers))
} else {
    String.format(Locale.US, "%d μm", micrometers)
}


fun maxOf(distances: Collection<Distance>) = distances.maxBy { it.value() }

fun sumOf(distances: Collection<Distance>) = distances.fold(ZERO) { sum, distance ->
    sum + distance
}
