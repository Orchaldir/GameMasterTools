package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.core.model.economy.material.MaterialId

class WeightPerMaterial(
    private val map: MutableMap<MaterialId, Weight> = mutableMapOf(),
) {

    fun add(material: MaterialId, weight: Weight): WeightPerMaterial {
        map.compute(material) { _, oldWeight ->
            if (oldWeight != null) {
                oldWeight + weight
            } else {
                weight
            }
        }

        return this
    }

    fun get(material: MaterialId) = map[material]

}
