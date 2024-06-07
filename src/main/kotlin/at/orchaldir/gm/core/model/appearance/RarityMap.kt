package at.orchaldir.gm.core.model.appearance

import kotlinx.serialization.Serializable

@Serializable
data class RarityMap<T>(val map: Map<T, Rarity>) {
    constructor(values: Collection<T>) : this(values.associateWith { Rarity.Common })

    init {
        require(hasValidValues()) { "RarityMap has no valid value!" }
    }

    fun hasValidValues() = map.values.any { it != Rarity.Unavailable }

    fun isAvailable(value: T) = (map[value] ?: Rarity.Unavailable) != Rarity.Unavailable

}