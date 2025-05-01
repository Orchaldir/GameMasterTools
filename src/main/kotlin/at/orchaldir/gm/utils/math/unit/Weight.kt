package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable
import java.util.Locale

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
        fun fromMilligrams(mg: Long) = Weight(mg)

        fun from(prefix: SiPrefix, value: Int) = Weight(
            when (prefix) {
                SiPrefix.Kilo -> downTwice(value)
                SiPrefix.Base -> down(value)
                SiPrefix.Milli -> value.toLong()
                SiPrefix.Micro -> up(value).toLong()
            }
        )

        fun resolveUnit(prefix: SiPrefix) = when (prefix) {
            SiPrefix.Kilo -> "kg"
            SiPrefix.Base -> "g"
            SiPrefix.Milli -> "mg"
            SiPrefix.Micro -> "µg"
        }
    }

    override fun value() = milligrams
    override fun convertTo(prefix: SiPrefix) = when (prefix) {
        SiPrefix.Kilo -> upTwice(milligrams).toLong()
        SiPrefix.Base -> up(milligrams).toLong()
        SiPrefix.Milli -> milligrams
        SiPrefix.Micro -> down(milligrams)
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

fun convertFromKilograms(kg: Long) = downTwice(kg)
fun convertFromKilograms(kg: Float) = downTwice(kg)
fun convertFromGrams(grams: Long) = down(grams)
fun convertFromGrams(grams: Float) = down(grams)

fun toKilograms(milligrams: Long) = upTwice(milligrams)
fun toGrams(milligrams: Long) = up(milligrams)

fun formatWeight(milligrams: Long) = if (milligrams > SQUARED) {
    String.format(Locale.US, "%.1f kg", toKilograms(milligrams))
} else if (milligrams > FACTOR) {
    String.format(Locale.US, "%.1f g", toGrams(milligrams))
} else {
    String.format(Locale.US, "%d mg", milligrams)
}