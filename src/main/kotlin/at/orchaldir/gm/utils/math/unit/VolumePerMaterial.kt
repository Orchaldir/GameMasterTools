package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.economy.material.calculatePrice

class VolumePerMaterial(
    private val map: MutableMap<MaterialId, Volume> = mutableMapOf(),
) {

    fun add(material: MaterialId, volume: Volume): VolumePerMaterial {
        map.compute(material) { _, oldVolume ->
            if (oldVolume != null) {
                oldVolume + volume
            } else {
                volume
            }
        }

        return this
    }

    fun get(material: MaterialId) = map[material]

    fun getMap(): Map<MaterialId, Volume> = map

    fun getPrice(state: State) = calculatePrice(state, getWeightPerMaterial(state))

    fun getWeightPerMaterial(state: State) = map.mapValues { (id, volume) ->
        val material = state.getMaterialStorage().getOrThrow(id)

        Weight.fromVolume(volume, material.density)
    }

    fun getWeight(state: State) = getWeightPerMaterial(state).values
        .reduceOrNull { acc, weight -> acc + weight }
        ?: WEIGHTLESS

}
