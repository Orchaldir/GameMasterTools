package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.appearance.Rarity
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.utils.NumberGenerator

data class RarityGenerator(val values: Map<Rarity, UInt>) {

    fun <T> generate(map: RarityMap<T>, numberGenerator: NumberGenerator): T? {
        val pair = calculateLookupMap(map)
        val threshold = pair.first

        return select(threshold, pair.second, numberGenerator)
    }

    fun select(maps: List<RarityMap<*>>, numberGenerator: NumberGenerator): UInt? {
        val thresholds = maps.map { calculateLookupMap(it) }
            .mapIndexed { index, pair -> Pair(index.toUInt(), pair.first) }
        val total = thresholds.sumOf { it.second }

        return select(total, thresholds, numberGenerator)
    }

    private fun <T> select(
        threshold: UInt,
        lookup: List<Pair<UInt, T>>,
        numberGenerator: NumberGenerator,
    ): T? {
        if (threshold == 0u) {
            return null
        }

        val index = numberGenerator.getNumber() % threshold

        lookup.forEach {
            if (index < it.first) {
                return it.second
            }
        }

        return null
    }

    private fun <T> calculateLookupMap(map: RarityMap<T>): Pair<UInt, List<Pair<UInt, T>>> {
        var threshold = 0u
        val lookup: List<Pair<UInt, T>> = map.map.entries
            .filter { it.value != Rarity.Unavailable }
            .map {
                val value = values[it.value] ?: 0u
                threshold += value
                Pair(threshold, it.key)
            }.toList()

        return Pair(threshold, lookup)
    }

}