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
        fun fromKilogram(kg: Int) = Weight(fromKilograms(kg))
        fun fromKilogram(kg: Float) = Weight(fromKilograms(kg))
        fun fromGram(g: Int) = Weight(fromGrams(g))
        fun fromGram(g: Float) = Weight(fromGrams(g))
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

fun fromKilograms(kg: Int) = downTwice(kg)
fun fromKilograms(kg: Float) = downTwice(kg)
fun fromGrams(grams: Int) = down(grams)
fun fromGrams(grams: Float) = down(grams)

fun toKilograms(milligrams: Int) = upTwice(milligrams)

fun formatWeight(milligrams: Int) = if (milligrams > SQUARED) {
    String.format(Locale.US, "%.1f kg", micrometersToMeter(milligrams))
} else if (milligrams > FACTOR) {
    String.format(Locale.US, "%.1f g", micrometersToMillimeter(milligrams))
} else {
    String.format(Locale.US, "%d mg", milligrams)
}