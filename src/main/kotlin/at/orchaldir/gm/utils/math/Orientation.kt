package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable
import kotlin.math.cos
import kotlin.math.sin

val QUARTER = Orientation.fromDegree(90.0f)

@JvmInline
@Serializable
value class Orientation private constructor(private val degree: Float) {

    companion object {
        fun fromDegree(degree: Float) = Orientation(degree)

        fun fromRadians(radians: Float) = Orientation(Math.toDegrees(radians.toDouble()).toFloat())

        fun zero() = Orientation(0.0f)
    }

    fun toDegree() = degree
    fun toRadians() = Math.toRadians(degree.toDouble()).toFloat()

    fun cos() = cos(toRadians())
    fun sin() = sin(toRadians())

    operator fun unaryMinus() = fromDegree(-degree)

    operator fun plus(other: Orientation) = fromDegree(degree + other.degree)
    operator fun minus(other: Orientation) = fromDegree(degree - other.degree)
    operator fun times(factor: Float) = fromDegree(degree * factor)
    operator fun div(factor: Int) = fromDegree(degree / factor)
}
