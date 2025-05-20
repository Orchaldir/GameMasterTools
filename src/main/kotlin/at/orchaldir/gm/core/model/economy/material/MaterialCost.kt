package at.orchaldir.gm.core.model.economy.material

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class MaterialCost private constructor(
    val map: Map<MaterialId, Int> = emptyMap(),
) {
    constructor() : this(emptyMap())
    constructor(material: MaterialId) : this(mapOf(material to 1))

    companion object {
        fun init(map: Map<MaterialId, Int>) = MaterialCost(map.filterValues { it > 0 })
    }

    fun contains(material: MaterialId) = map.containsKey(material)

    fun materials() = map.keys
}
