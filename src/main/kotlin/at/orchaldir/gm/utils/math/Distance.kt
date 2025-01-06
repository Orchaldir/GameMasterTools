package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

private const val FACTOR = 1000

@Serializable
data class Distance(val millimeters: Int) {

    init {
        require(millimeters >= 0) { "Distance must be greater 0!" }
    }

    companion object {
        fun fromMeters(meters: Float) = Distance(meterToMillimeter(meters))
    }

    fun metersOnly() = metersOnly(millimeters)
    fun millimetersOnly() = millimetersOnly(millimeters)

    fun toMeters() = millimeterToMeter(millimeters)
    override fun toString() = formatMillimetersAsMeters(millimeters)

    operator fun plus(other: Distance) = Distance(millimeters + other.millimeters)
    operator fun minus(other: Distance) = Distance(millimeters - other.millimeters)
    operator fun times(factor: Float) = Distance((millimeters * factor).toInt())
    operator fun times(factor: Factor) = times(factor.value)
    operator fun times(factor: Int) = Distance(millimeters * factor)

    fun max(other: Distance) = if (millimeters >= other.millimeters) {
        this
    } else {
        other
    }
}

fun metersOnly(millimeters: Int) = millimeters / FACTOR
fun millimetersOnly(millimeters: Int) = millimeters % FACTOR

fun meterToMillimeter(meter: Float) = (meter * FACTOR).toInt()
fun millimeterToMeter(millimeters: Int) = millimeters / FACTOR.toFloat()

fun formatMillimetersAsMeters(millimeters: Int) =
    String.format("%d.%03d m", metersOnly(millimeters), millimetersOnly(millimeters))
