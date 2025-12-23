package at.orchaldir.gm.core.model.economy.material

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.utils.math.unit.Weight
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class MaterialCost(
    val map: Map<MaterialId, Weight> = emptyMap(),
) {
    constructor() : this(emptyMap())
    constructor(material: MaterialId) : this(mapOf(material to Weight.fromKilograms(1)))

    companion object {
        fun init(map: Map<MaterialId, Weight>) = MaterialCost(
            map.filterValues { it.value() > 0 }
        )
    }

    fun contains(material: MaterialId) = map.containsKey(material)

    fun materials() = map.keys

    fun calculatePrice(state: State) = calculatePrice(state, map)
    fun calculateWeight() = map.values.reduceOrNull { sum, weight -> sum + weight }
}

fun calculatePrice(state: State, map: Map<MaterialId, Weight>): Price {
    var total = Price(0)

    map.forEach { (id, weight) ->
        val material = state.getMaterialStorage().getOrThrow(id)

        total += Price.fromWeight(weight, material.pricePerKilogram)
    }

    return total
}