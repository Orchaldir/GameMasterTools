package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.core.model.economy.material.MaterialId

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

}
