package at.orchaldir.gm.core.model.economy.material

import at.orchaldir.gm.utils.math.unit.Weight
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class MaterialCost(
    val map: Map<MaterialId, Weight> = emptyMap(),
) {
    constructor() : this(emptyMap())
    constructor(material: MaterialId) : this(mapOf(material to Weight.fromKilograms(1)))

    fun contains(material: MaterialId) = map.containsKey(material)

    fun materials() = map.keys

    fun calculateWeight() = map.values.reduceOrNull { sum, weight -> sum + weight }
}
