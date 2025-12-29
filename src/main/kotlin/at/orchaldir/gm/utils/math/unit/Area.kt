package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Size2d
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.math.PI
import kotlin.math.pow

val ZERO_AREA = Area.fromSquareMillimeters(0)

@JvmInline
@Serializable
value class Area private constructor(private val smm: Long) : SiUnit<Area> {

    init {
        require(smm >= 0) { "Area $smm must be greater 0!" }
    }

    companion object {
        fun fromSquareKilometers(skm: Long) = Area(convertFromSquareKilometers(skm))
        fun fromSquareKilometers(skm: Float) = Area(convertFromSquareKilometers(skm))
        fun fromSquareMeters(sm: Long) = Area(convertFromSquareMeters(sm))
        fun fromSquareMeters(sm: Float) = Area(convertFromSquareMeters(sm))
        fun fromSquareDecimeters(sdm: Long) = Area(convertFromSquareDecimeters(sdm))
        fun fromSquareDecimeters(sdm: Float) = Area(convertFromSquareDecimeters(sdm))
        fun fromSquareCentimeters(scm: Long) = Area(convertFromSquareCentimeters(scm))
        fun fromSquareCentimeters(scm: Float) = Area(convertFromSquareCentimeters(scm))
        fun fromSquareMillimeters(smm: Long) = Area(smm)
        fun fromSquareMillimeters(smm: Float) = Area(smm.toLong())
        fun fromSquareMicrometers(sµm: Long) = Area(convertFromSquareMicrometers(sµm).toLong())

        fun from(prefix: SiPrefix, value: Long) = when (prefix) {
            SiPrefix.Kilo -> fromSquareKilometers(value)
            SiPrefix.Base -> fromSquareMeters(value)
            SiPrefix.Centi -> fromSquareCentimeters(value)
            SiPrefix.Milli -> fromSquareMillimeters(value)
            SiPrefix.Micro -> fromSquareMicrometers(value)
        }

        // shapes

        fun fromSquare(size: Size2d) = fromSquare(size.width, size.height)

        fun fromSquare(width: Distance, height: Distance) =
            fromSquareMeters(width.toMeters() * height.toMeters())

        fun fromCircle(radius: Distance) =
            fromSquareMeters(radius.toMeters().pow(2) * PI.toFloat())
    }

    override fun value() = smm

    fun toSquareMeters() = convertToSquareMeters(smm)

    override fun convertToLong(prefix: SiPrefix) = when (prefix) {
        SiPrefix.Kilo -> convertToSquareKilometers(smm).toLong()
        SiPrefix.Base -> convertToSquareMeters(smm).toLong()
        SiPrefix.Centi -> convertToSquareCentimeters(smm).toLong()
        SiPrefix.Milli -> smm
        SiPrefix.Micro -> convertToSquareMicrometers(smm)
    }

    override fun toString() = formatArea(smm)

    override operator fun plus(other: Area) = Area(smm + other.smm)
    override operator fun minus(other: Area) = Area(smm - other.smm)
    operator fun times(factor: Float) = Area((smm * factor).toLong())
    override operator fun times(factor: Factor) = times(factor.toNumber())
    operator fun times(factor: Int) = Area(smm * factor)
    operator fun div(factor: Float) = Area((smm / factor).toLong())
    operator fun div(factor: Int) = Area(smm / factor)

    fun max(other: Area) = if (smm >= other.smm) {
        this
    } else {
        other
    }
}

fun convertFromSquareKilometers(skm: Long) = downSixSteps(downSixSteps(skm))
fun convertFromSquareKilometers(skm: Float) = downSixSteps(downSixSteps(skm))
fun convertFromSquareMeters(sm: Long) = downThreeSteps(downThreeSteps(sm))
fun convertFromSquareMeters(sm: Float) = downThreeSteps(downThreeSteps(sm))
fun convertFromSquareDecimeters(sdm: Long) = downTwoSteps(downTwoSteps(sdm))
fun convertFromSquareDecimeters(sdm: Float) =  downTwoSteps(downTwoSteps(sdm))
fun convertFromSquareCentimeters(scm: Long) = down(down(scm))
fun convertFromSquareCentimeters(scm: Float) = down(down(scm))
fun convertFromSquareMicrometers(cµm: Long) = upThreeSteps(upThreeSteps(cµm))

fun convertToSquareKilometers(smm: Long) = upSixSteps(upSixSteps(smm))
fun convertToSquareMeters(smm: Long) = upThreeSteps(upThreeSteps(smm))
fun convertToSquareDecimeters(smm: Long) = upTwoSteps(upTwoSteps(smm))
fun convertToSquareCentimeters(smm: Long) = up(up(smm))
fun convertToSquareMicrometers(smm: Long) = downThreeSteps(downThreeSteps(smm))

fun formatArea(smm: Long) = if (smm >= SI_TWELVE_STEPS) {
    String.format(Locale.US, "%.1f km^2", convertToSquareKilometers(smm))
} else if (smm >= SI_SIX_STEPS) {
    String.format(Locale.US, "%.1f m^2", convertToSquareMeters(smm))
} else if (smm >= SI_FOUR_STEPS) {
    String.format(Locale.US, "%.1f dm^2", convertToSquareDecimeters(smm))
} else if (smm >= SI_TWO_STEPS) {
    String.format(Locale.US, "%.1f cm^2", convertToSquareCentimeters(smm))
} else {
    String.format(Locale.US, "%d mm^2", smm)
}