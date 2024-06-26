package at.orchaldir.gm.core.model.appearance

import kotlinx.serialization.Serializable

val ONE_OF_RARITIES = Rarity.entries.filter { it != Rarity.Everyone }.toSet()
private val SOME_OF_RARITIES = Rarity.entries.toSet()

interface RarityMap<T> {

    fun getAvailableRarities(): Set<Rarity>

    fun getValidValues(): Map<T, Rarity>

    fun getRarityFor(keys: Set<T>): Map<T, Rarity>

    fun isAvailable(value: T): Boolean
}

/**
 * A rarity map, where only 1 value is selected
 */
@JvmInline
@Serializable
value class OneOf<T>(private val map: Map<T, Rarity>) : RarityMap<T> {
    constructor(values: Collection<T>) : this(values.associateWith { Rarity.Common })

    init {
        require(hasValidValues()) { "RarityMap has no valid value!" }
    }

    companion object {

        fun <T> init(map: Map<T, Rarity>) = OneOf(map.filterValues { it != Rarity.Unavailable })

    }

    override fun getAvailableRarities() = ONE_OF_RARITIES

    override fun getValidValues() = map

    override fun getRarityFor(keys: Set<T>) = keys.associateWith { map[it] ?: Rarity.Unavailable }

    override fun isAvailable(value: T) = (map[value] ?: Rarity.Unavailable) != Rarity.Unavailable

    private fun hasValidValues() = map.values.any { it != Rarity.Unavailable }

}

/**
 * A rarity map, where several values can be selected
 */
@JvmInline
@Serializable
value class SomeOf<T>(private val map: Map<T, Rarity>) : RarityMap<T> {
    constructor(values: Collection<T>) : this(values.associateWith { Rarity.Common })

    companion object {

        fun <T> init(map: Map<T, Rarity>) = SomeOf(map.filterValues { it != Rarity.Unavailable })

    }

    override fun getAvailableRarities() = SOME_OF_RARITIES

    override fun getValidValues() = map

    override fun getRarityFor(keys: Set<T>) = keys.associateWith { map[it] ?: Rarity.Unavailable }

    override fun isAvailable(value: T) = (map[value] ?: Rarity.Unavailable) != Rarity.Unavailable

}

fun <T> reverseAndSort(map: Map<T, Rarity>) =
    map
        .toList()
        .groupBy { p -> p.second }
        .mapValues { p -> p.value.map { it.first } }
        .toSortedMap()