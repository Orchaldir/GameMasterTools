package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable
import java.util.*

@JvmInline
@Serializable
value class Volume private constructor(private val cmm: Long) : SiUnit<Volume> {

    init {
        require(cmm >= 0) { "Volume must be greater 0!" }
    }

    companion object {
        fun fromCubicMeters(cm: Long) = Volume(convertFromCubicMeters(cm))
        fun fromCubicDecimeters(cdm: Long) = Volume(convertFromCubicDecimeters(cdm))
        fun fromCubicCentimeters(ccm: Long) = Volume(convertFromCubicCentimeters(ccm))
        fun fromCubicMillimeters(cmm: Long) = Volume(cmm)
        fun fromCubicMicrometers(cmm: Long) = Volume(convertFromCubicMicrometers(cmm).toLong())
    }

    override fun value() = cmm
    override fun convertToLong(prefix: SiPrefix) = when (prefix) {
        SiPrefix.Kilo -> convertToCubicKilometers(cmm).toLong()
        SiPrefix.Base -> convertToCubicMeters(cmm).toLong()
        SiPrefix.Centi -> convertToCubicCentimeters(cmm).toLong()
        SiPrefix.Milli -> cmm
        SiPrefix.Micro -> convertToCubicMicrometers(cmm)
    }

    override fun toString() = formatVolume(cmm)

    override operator fun plus(other: Volume) = Volume(cmm + other.cmm)
    override operator fun minus(other: Volume) = Volume(cmm - other.cmm)
    operator fun times(factor: Float) = Volume((cmm * factor).toLong())
    override operator fun times(factor: Factor) = times(factor.toNumber())
    operator fun times(factor: Int) = Volume(cmm * factor)
    operator fun div(factor: Float) = Volume((cmm / factor).toLong())
    operator fun div(factor: Int) = Volume(cmm / factor)

    fun max(other: Volume) = if (cmm >= other.cmm) {
        this
    } else {
        other
    }
}

fun convertFromCubicMeters(cm: Long) = downNineSteps(cm)
fun convertFromCubicDecimeters(cdm: Long) = downSixSteps(cdm)
fun convertFromCubicCentimeters(ccm: Long) = downThreeSteps(ccm)
fun convertFromCubicMicrometers(cµm: Long) = upSixSteps(upSixSteps(upSixSteps(cµm)))

fun convertToCubicKilometers(ckm: Long) = upSixSteps(upSixSteps(upSixSteps(ckm)))
fun convertToCubicMeters(cmm: Long) = upNineSteps(cmm)
fun convertToCubicDecimeters(cmm: Long) = upSixSteps(cmm)
fun convertToCubicCentimeters(cmm: Long) = upThreeSteps(cmm)
fun convertToCubicMicrometers(cmm: Long) = downSixSteps(downSixSteps(downSixSteps(cmm)))

fun formatVolume(cmm: Long) = if (cmm >= SI_NINE_STEPS) {
    String.format(Locale.US, "%.1f m^3", convertToCubicMeters(cmm))
} else if (cmm >= SI_SIX_STEPS) {
    String.format(Locale.US, "%.1f dm^3", convertToCubicDecimeters(cmm))
} else if (cmm >= SI_THREE_STEPS) {
    String.format(Locale.US, "%.1f cm^3", convertToCubicCentimeters(cmm))
} else {
    String.format(Locale.US, "%d mm^3", cmm)
}