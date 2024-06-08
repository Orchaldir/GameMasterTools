package at.orchaldir.gm.core.model.appearance

import kotlinx.serialization.Serializable

@Serializable
data class RarityMap<T>(private val map: Map<T, Rarity>) {
    constructor(values: Collection<T>) : this(values.associateWith { Rarity.Common })

    init {
        require(hasValidValues()) { "RarityMap has no valid value!" }
    }

    companion object {

        fun <T> init(map: Map<T, Rarity>) = RarityMap(map.filterValues { it != Rarity.Unavailable })

    }

    fun getValidValues() = map

    fun getRarityFor(keys: Set<T>) = keys.associateWith { map[it] ?: Rarity.Unavailable }

    private fun hasValidValues() = map.values.any { it != Rarity.Unavailable }

    fun isAvailable(value: T) = (map[value] ?: Rarity.Unavailable) != Rarity.Unavailable

}

fun <T> reverseAndSort(map: Map<T, Rarity>) =
    map
        .toList()
        .groupBy { p -> p.second }
        .mapValues { p -> p.value.map { it.first } }
        .toSortedMap()