package at.orchaldir.gm.utils.math.unit

import kotlinx.serialization.Serializable
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

val ZERO_ORIENTATION = Orientation.fromDegrees(0)
val QUARTER_CIRCLE = Orientation.fromDegrees(90)
val ONE_THIRD_CIRCLE = Orientation.fromDegrees(120)
val HALF_CIRCLE = Orientation.fromDegrees(180)
val TWO_THIRD_CIRCLE = Orientation.fromDegrees(240)
val FULL_CIRCLE = Orientation.fromDegrees(360)

@JvmInline
@Serializable
value class Orientation private constructor(private val millidegrees: Long) {

    companion object {
        fun fromDegrees(degrees: Float) = Orientation(convertFromDegrees(degrees))
        fun fromDegrees(degrees: Long) = Orientation(convertFromDegrees(degrees))
        fun fromMillidegrees(degrees: Long) = Orientation(degrees)

        fun fromRadians(radians: Float) = fromDegrees(Math.toDegrees(radians.toDouble()).toFloat())

        fun zero() = ZERO_ORIENTATION
    }

    fun value() = millidegrees

    fun toDegrees() = convertToDegrees(millidegrees)
    fun toRadians() = Math.toRadians(toDegrees().toDouble()).toFloat()

    fun isZero() = millidegrees == 0L

    fun normalizeZeroToTwoPi(): Orientation {
        val degree = toDegrees()
        return fromDegrees(
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

    operator fun unaryMinus() = Orientation(-millidegrees)

    operator fun plus(other: Orientation) = Orientation(millidegrees + other.millidegrees)
    operator fun minus(other: Orientation) = Orientation(millidegrees - other.millidegrees)
    operator fun times(factor: Float) = Orientation((millidegrees * factor).toLong())
    operator fun div(factor: Int) = Orientation(millidegrees / factor)
    operator fun div(factor: Float) = Orientation((millidegrees / factor).toLong())

    override fun toString() = formatOrientation(millidegrees)
}

fun convertFromDegrees(degrees: Long) = downThreeSteps(degrees)
fun convertFromDegrees(degrees: Float) = downThreeSteps(degrees)

fun convertToDegrees(millidegrees: Long) = upThreeSteps(millidegrees)

fun formatOrientation(millidegrees: Long) = if (millidegrees >= SI_THREE_STEPS) {
    String.format(Locale.US, "%.1f°", convertToDegrees(millidegrees))
} else {
    String.format(Locale.US, "%d m°", millidegrees)
}
