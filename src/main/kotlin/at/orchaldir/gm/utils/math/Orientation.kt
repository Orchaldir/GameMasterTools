package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

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
}
