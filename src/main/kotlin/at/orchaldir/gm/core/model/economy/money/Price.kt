package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Weight
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Price(val value: Int) {

    private constructor(value: Float): this(value.toInt())

    companion object {

        fun fromWeight(weight: Weight, pricePerKilogram: Price) =
            Price(weight.toKilograms() * pricePerKilogram.value)
        
    }
    
    init {
        require(value >= 0) { "Price is negative!" }
    }

    operator fun plus(other: Price) = Price(value + other.value)
    operator fun minus(other: Price) = Price(value - other.value)
    operator fun times(factor: Float) = Price(value * factor)
    operator fun times(factor: Factor) = times(factor.toNumber())
    operator fun times(factor: Int) = Price(value * factor)
    operator fun div(factor: Float) = Price(value / factor)
    operator fun div(factor: Int) = Price(value / factor)
}
