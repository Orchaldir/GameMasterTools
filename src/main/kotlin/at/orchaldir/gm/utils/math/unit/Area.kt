package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Size2d
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.math.PI
import kotlin.math.pow

val ZERO_AREA = Area.fromSquareMeters(0)

@JvmInline
@Serializable
value class Area private constructor(private val sm: Float) {

    init {
        require(sm >= 0) { "Area $sm must be greater 0!" }
    }

    companion object {
        fun fromSquareKiloMeters(skm: Long) = fromSquareKiloMeters(skm.toFloat())
        fun fromSquareKiloMeters(skm: Float) = convertToSquareKilometers(skm)
        fun fromSquareMeters(sm: Long) = Area(sm.toFloat())
        fun fromSquareMeters(sm: Float) = Area(sm)

        fun convertFrom(value: Float, prefix: SiPrefix) = Area(when (prefix) {
            SiPrefix.Kilo -> convertFromSquareKilometers(value)
            SiPrefix.Base -> value
            SiPrefix.Centi -> convertFromSquareCentimeters(value)
            SiPrefix.Milli -> convertFromSquareMillimeters(value)
            SiPrefix.Micro -> error("Unsupported!")
        })

        // shapes

        fun fromSquare(size: Size2d) = fromSquare(size.width, size.height)

        fun fromSquare(width: Distance, height: Distance) =
            fromSquareMeters(width.toMeters() * height.toMeters())

        fun fromCircle(radius: Distance) =
            fromSquareMeters(radius.toMeters().pow(2) * PI.toFloat())
    }

    fun convertTo(prefix: SiPrefix) = when (prefix) {
        SiPrefix.Kilo -> convertToSquareKilometers(sm)
        SiPrefix.Base -> sm
        SiPrefix.Centi -> convertToSquareCentimeters(sm)
        SiPrefix.Milli -> convertToSquareMillimeters(sm)
        SiPrefix.Micro -> error("Unsupported!")
    }

    override fun toString() = formatArea(sm)

    operator fun plus(other: Area) = Area(sm + other.sm)
    operator fun minus(other: Area) = Area(sm - other.sm)
    operator fun times(factor: Float) = Area(sm * factor)
    operator fun times(factor: Factor) = times(factor.toNumber())
    operator fun times(factor: Int) = Area(sm * factor)
    operator fun div(factor: Float) = Area(sm / factor)
    operator fun div(factor: Int) = Area(sm / factor)

    fun max(other: Area) = if (sm >= other.sm) {
        this
    } else {
        other
    }
}

fun convertFromSquareKilometers(skm: Float) = skm * SI_SIX_STEPS
fun convertFromSquareCentimeters(skm: Float) = skm / SI_TWO_STEPS
fun convertFromSquareMillimeters(skm: Float) = skm / SI_SIX_STEPS

fun convertToSquareKilometers(sm: Float) = sm / SI_SIX_STEPS
fun convertToSquareCentimeters(sm: Float) = sm * SI_TWO_STEPS
fun convertToSquareMillimeters(sm: Float) = sm * SI_SIX_STEPS

fun formatArea(sm: Float) = if (sm >= SI_SIX_STEPS) {
    String.format(Locale.US, "%.1f km^2", convertToSquareKilometers(sm))
} else {
    String.format(Locale.US, "%.1f m^2", sm)
}