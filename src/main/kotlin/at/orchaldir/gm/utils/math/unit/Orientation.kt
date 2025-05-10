package at.orchaldir.gm.utils.math.unit

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

val ZERO_ORIENTATION = Orientation.fromDegree(0)
val QUARTER_CIRCLE = Orientation.fromDegree(90)
val ONE_THIRD_CIRCLE = Orientation.fromDegree(120)
val HALF_CIRCLE = Orientation.fromDegree(180)
val TWO_THIRD_CIRCLE = Orientation.fromDegree(240)
val FULL_CIRCLE = Orientation.fromDegree(360)

@JvmInline
@Serializable
value class Orientation private constructor(private val millidegree: Long) {

    companion object {
        fun fromDegree(degree: Float) = Orientation(convertFromDegrees(degree))
        fun fromDegree(degree: Long) = Orientation(convertFromDegrees(degree))

        fun fromRadians(radians: Float) = fromDegree(Math.toDegrees(radians.toDouble()).toFloat())

        fun zero() = ZERO_ORIENTATION
    }

    fun value() = millidegree

    fun toDegree() = convertToDegrees(millidegree)
    fun toRadians() = Math.toRadians(toDegree().toDouble()).toFloat()

    fun isZero() = millidegree == 0L

    fun normalizeZeroToTwoPi(): Orientation {
        val degree = toDegree()
        return fromDegree(
            if (degree < 0.0f) {
                val n = ceil(degree.absoluteValue / 360.0f)
                degree + 360.0f * n
            } else {
                degree
            } % 360.0f
        )
    }

    fun cos() = cos(toRadians())
    fun sin() = sin(toRadians())

    operator fun unaryMinus() = Orientation(-millidegree)

    operator fun plus(other: Orientation) = Orientation(millidegree + other.millidegree)
    operator fun minus(other: Orientation) = Orientation(millidegree - other.millidegree)
    operator fun times(factor: Float) = Orientation((millidegree * factor).toLong())
    operator fun div(factor: Int) = Orientation(millidegree / factor)
    operator fun div(factor: Float) = Orientation((millidegree / factor).toLong())
}

fun convertFromDegrees(degrees: Long) = downThreeSteps(degrees)
fun convertFromDegrees(degrees: Float) = downThreeSteps(degrees)

fun convertToDegrees(millidegree: Long) = upThreeSteps(millidegree)
