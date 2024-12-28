package at.orchaldir.gm.core.model.material

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class MaterialCost private constructor(
    val map: Map<Material, UInt> = emptyMap(),
) {
    constructor() : this(emptyMap())

    companion object {
        fun fromSigned(map: Map<Material, UInt>) = MaterialCost(map.filterValues { it > 0u })
        fun fromUnsigned(map: Map<Material, Int>) = MaterialCost(
            map
            .filterValues { it > 0 }
            .mapValues { it.value.toUInt() })
    }
}
