package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.appearance.ONE_OF_RARITIES
import at.orchaldir.gm.core.model.appearance.Rarity
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.utils.NumberGenerator

data class RarityGenerator(val values: Map<Rarity, UInt>) {

    companion object {

        fun empty(diff: UInt = 1u): RarityGenerator {
            var value = 0u
            return RarityGenerator(ONE_OF_RARITIES.toList().reversed().associateWith {
                val current = value
                value += diff
                current
            })
        }

    }

    init {
        ONE_OF_RARITIES.filter { it != Rarity.Unavailable }
            .forEach {
                val value = values[it]
                require(value != null && value > 0u) { "Rarity $it has no valid value!" }
            }
    }

    fun <T> generate(map: RarityMap<T>, numberGenerator: NumberGenerator): T {
        val pair = calculateLookupMap(map)
        val threshold = pair.first

        return select(threshold, pair.second, numberGenerator)
    }

    fun select(maps: List<RarityMap<*>>, numberGenerator: NumberGenerator): UInt {
        val thresholds = maps.map { calculateLookupMap(it) }
            .mapIndexed { index, pair -> Pair(index.toUInt(), pair.first) }
        val total = thresholds.sumOf { it.second }

        return select(total, thresholds, numberGenerator)
    }

    private fun <T> select(
        threshold: UInt,
        lookup: List<Pair<UInt, T>>,
        numberGenerator: NumberGenerator,
    ): T {
        val index = numberGenerator.getNumber() % threshold

        lookup.forEach {
            if (index < it.first) {
                return it.second
            }
        }

        error("There should always be a valid value!")
    }

    private fun <T> calculateLookupMap(map: RarityMap<T>): Pair<UInt, List<Pair<UInt, T>>> {
        var threshold = 0u
        val lookup: List<Pair<UInt, T>> = map.getRarityMap()
            .entries
            .map {
                val value = values[it.value] ?: 0u
                threshold += value
                Pair(threshold, it.key)
            }.toList()

        return Pair(threshold, lookup)
    }

}