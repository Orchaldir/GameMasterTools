package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable
import java.util.*

val WEIGHTLESS = Weight.fromKilograms(0)

private const val FACTOR = 1000
private const val SQUARED = FACTOR * FACTOR

@JvmInline
@Serializable
value class Weight private constructor(private val milligrams: Long) : SiUnit<Weight> {

    init {
        require(milligrams >= 0) { "Weight must be greater 0!" }
    }

    companion object {
        fun fromKilograms(kg: Long) = Weight(convertFromKilograms(kg))
        fun fromKilograms(kg: Float) = Weight(convertFromKilograms(kg))
        fun fromGrams(g: Long) = Weight(convertFromGrams(g))
        fun fromGrams(g: Float) = Weight(convertFromGrams(g))
        fun fromCentigrams(g: Long) = Weight(convertFromCentigrams(g))
        fun fromMilligrams(mg: Long) = Weight(mg)
        fun fromMicrograms(micrograms: Long) = Weight(convertFromMicrograms(micrograms))

        fun fromVolume(volume: Float, density: Weight) =
            fromKilograms(volume * density.toKilograms())

        fun from(prefix: SiPrefix, value: Long) = when (prefix) {
            SiPrefix.Kilo -> fromKilograms(value)
            SiPrefix.Base -> fromGrams(value)
            SiPrefix.Centi -> fromCentigrams(value)
            SiPrefix.Milli -> fromMilligrams(value)
            SiPrefix.Micro -> fromMicrograms(value)
        }

        fun resolveUnit(prefix: SiPrefix) = prefix.resolveUnit() + "g"
    }

    override fun value() = milligrams
    override fun convertToLong(prefix: SiPrefix) = when (prefix) {
        SiPrefix.Kilo -> toKilograms(milligrams).toLong()
        SiPrefix.Base -> toGrams(milligrams).toLong()
        SiPrefix.Centi -> toCentigrams(milligrams).toLong()
        SiPrefix.Milli -> milligrams
        SiPrefix.Micro -> toMicrograms(milligrams)
    }

    fun toKilograms() = toKilograms(milligrams)

    override fun toString() = formatWeight(milligrams)

    override operator fun plus(other: Weight) = Weight(milligrams + other.milligrams)
    override operator fun minus(other: Weight) = Weight(milligrams - other.milligrams)
    operator fun times(factor: Float) = Weight((milligrams * factor).toLong())
    operator fun times(factor: Factor) = times(factor.toNumber())
    operator fun times(factor: Int) = Weight(milligrams * factor)
    operator fun div(factor: Float) = Weight((milligrams / factor).toLong())
    operator fun div(factor: Int) = Weight(milligrams / factor)

    fun max(other: Weight) = if (milligrams >= other.milligrams) {
        this
    } else {
        other
    }
}

fun convertFromKilograms(kg: Long) = downSixSteps(kg)
fun convertFromKilograms(kg: Float) = downSixSteps(kg)
fun convertFromGrams(grams: Long) = downThreeSteps(grams)
fun convertFromGrams(grams: Float) = downThreeSteps(grams)
fun convertFromCentigrams(grams: Long) = down(grams)
fun convertFromMicrograms(micrograms: Long) = upThreeSteps(micrograms).toLong()

fun toKilograms(milligrams: Long) = upSixSteps(milligrams)
fun toGrams(milligrams: Long) = upThreeSteps(milligrams)
fun toCentigrams(milligrams: Long) = up(milligrams)
fun toMicrograms(milligrams: Long) = downThreeSteps(milligrams)

fun formatWeight(milligrams: Long) = if (milligrams >= SQUARED) {
    String.format(Locale.US, "%.1f kg", toKilograms(milligrams))
} else if (milligrams >= FACTOR) {
    String.format(Locale.US, "%.1f g", toGrams(milligrams))
} else {
    String.format(Locale.US, "%d mg", milligrams)
}