package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

private const val FACTOR = 1000

@Serializable
data class Weight(val grams: Int) {

    init {
        require(grams >= 0) { "Weight must be greater 0!" }
    }

    companion object {
        fun fromKilogram(kg: Float) = Weight(kilogramsToGrams(kg))
    }

    fun kilogramsOnly() = kilogramsOnly(grams)
    fun gramsOnly() = gramsOnly(grams)

    fun toKilograms() = gramsToKilograms(grams)
    override fun toString() = formatAsKilograms(grams)

    operator fun plus(other: Weight) = Weight(grams + other.grams)
    operator fun minus(other: Weight) = Weight(grams - other.grams)
    operator fun times(factor: Float) = Weight((grams * factor).toInt())
    operator fun times(factor: Factor) = times(factor.value)
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

fun formatAsKilograms(grams: Int) =
    String.format("%d.%03d kg", kilogramsOnly(grams), gramsOnly(grams))
