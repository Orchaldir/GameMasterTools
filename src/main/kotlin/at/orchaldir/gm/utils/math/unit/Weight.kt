package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable
import java.util.Locale

private const val FACTOR = 1000
private const val SQUARED = FACTOR * FACTOR

@JvmInline
@Serializable
value class Weight private constructor(private val milligrams: Int) : SiUnit<Weight> {

    init {
        require(milligrams >= 0) { "Weight must be greater 0!" }
    }

    companion object {
        fun fromKilograms(kg: Int) = Weight(convertFromKilograms(kg))
        fun fromKilograms(kg: Float) = Weight(convertFromKilograms(kg))
        fun fromGrams(g: Int) = Weight(convertFromGrams(g))
        fun fromGrams(g: Float) = Weight(convertFromGrams(g))
        fun fromMilligrams(mg: Int) = Weight(mg)
    }

    override fun value() = milligrams

    fun toKilograms() = toKilograms(milligrams)

    override fun toString() = formatWeight(milligrams)

    override operator fun plus(other: Weight) = Weight(milligrams + other.milligrams)
    override operator fun minus(other: Weight) = Weight(milligrams - other.milligrams)
    operator fun times(factor: Float) = Weight((milligrams * factor).toInt())
    operator fun times(factor: Factor) = times(factor.toNumber())
    operator fun times(factor: Int) = Weight(milligrams * factor)
    operator fun div(factor: Float) = Weight((milligrams / factor).toInt())
    operator fun div(factor: Int) = Weight(milligrams / factor)

    fun max(other: Weight) = if (milligrams >= other.milligrams) {
        this
    } else {
        other
    }
}

fun convertFromKilograms(kg: Int) = downTwice(kg)
fun convertFromKilograms(kg: Float) = downTwice(kg)
fun convertFromGrams(grams: Int) = down(grams)
fun convertFromGrams(grams: Float) = down(grams)

fun toKilograms(milligrams: Int) = upTwice(milligrams)

fun formatWeight(milligrams: Int) = if (milligrams > SQUARED) {
    String.format(Locale.US, "%.1f kg", micrometersToMeter(milligrams))
} else if (milligrams > FACTOR) {
    String.format(Locale.US, "%.1f g", micrometersToMillimeter(milligrams))
} else {
    String.format(Locale.US, "%d mg", milligrams)
}