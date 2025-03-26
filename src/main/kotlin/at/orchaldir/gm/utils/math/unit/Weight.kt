package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

private const val FACTOR = 1000
private const val LESS_DIGITS_THRESHOLD = FACTOR * 10

@JvmInline
@Serializable
value class Weight private constructor(private val grams: Int) : SiUnit<Weight> {

    init {
        require(grams >= 0) { "Weight must be greater 0!" }
    }

    companion object {
        fun fromKilogram(kg: Float) = Weight(kilogramsToGrams(kg))
        fun fromGram(gram: Int) = Weight(gram)
    }

    override fun value() = grams

    fun kilogramsOnly() = kilogramsOnly(grams)
    fun gramsOnly() = gramsOnly(grams)

    fun toKilograms() = gramsToKilograms(grams)

    override fun toString() = formatAsKilograms(grams)

    override operator fun plus(other: Weight) = Weight(grams + other.grams)
    override operator fun minus(other: Weight) = Weight(grams - other.grams)
    operator fun times(factor: Float) = Weight((grams * factor).toInt())
    operator fun times(factor: Factor) = times(factor.toNumber())
    operator fun times(factor: Int) = Weight(grams * factor)
    operator fun div(factor: Float) = Weight((grams / factor).toInt())
    operator fun div(factor: Int) = Weight(grams / factor)

    fun max(other: Weight) = if (grams >= other.grams) {
        this
    } else {
        other
    }
}

fun kilogramsOnly(grams: Int) = grams / FACTOR
fun gramsOnly(grams: Int) = grams % FACTOR

fun kilogramsToGrams(kg: Float) = (kg * FACTOR).toInt()
fun gramsToKilograms(milligrams: Int) = milligrams / FACTOR.toFloat()

fun formatAsKilograms(grams: Int) = String.format(
    if (grams > LESS_DIGITS_THRESHOLD) {
        "%d.%01d kg"
    } else {
        "%d.%03d kg"
    },
    kilogramsOnly(grams),
    if (grams > LESS_DIGITS_THRESHOLD) {
        gramsOnly(grams) / 100
    } else {
        gramsOnly(grams)
    },
)
