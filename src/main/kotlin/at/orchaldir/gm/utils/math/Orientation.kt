package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

val ZERO_ORIENTATION = Orientation.fromDegree(0.0f)
val QUARTER_CIRCLE = Orientation.fromDegree(90.0f)
val ONE_THIRD_CIRCLE = Orientation.fromDegree(120.0f)
val HALF_CIRCLE = Orientation.fromDegree(180.0f)
val TWO_THIRD_CIRCLE = Orientation.fromDegree(240.0f)
val FULL_CIRCLE = Orientation.fromDegree(360.0f)

@JvmInline
@Serializable
value class Orientation private constructor(private val degree: Float) {

    companion object {
        fun fromDegree(degree: Float) = Orientation(degree)

        fun fromRadians(radians: Float) = Orientation(Math.toDegrees(radians.toDouble()).toFloat())

        fun zero() = ZERO_ORIENTATION
    }

    fun toDegree() = degree
    fun toRadians() = Math.toRadians(degree.toDouble()).toFloat()

    fun normalizeZeroToTwoPi() = Orientation(
        if (degree < 0.0f) {
            val n = ceil(degree.absoluteValue / 360.0f)
            degree + 360.0f * n
        } else {
            degree
        } % 360.0f
    )

    fun cos() = cos(toRadians())
    fun sin() = sin(toRadians())

    operator fun unaryMinus() = fromDegree(-degree)

    operator fun plus(other: Orientation) = fromDegree(degree + other.degree)
    operator fun minus(other: Orientation) = fromDegree(degree - other.degree)
    operator fun times(factor: Float) = fromDegree(degree * factor)
    operator fun div(factor: Int) = fromDegree(degree / factor)
    operator fun div(factor: Float) = fromDegree(degree / factor)
}
