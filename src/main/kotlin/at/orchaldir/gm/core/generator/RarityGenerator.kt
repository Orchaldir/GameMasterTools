package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.appearance.Rarity
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.utils.NumberGenerator

data class RarityGenerator(val values: Map<Rarity, UInt>) {

    fun <T> generate(map: RarityMap<T>, numberGenerator: NumberGenerator): T? {
        var threshold = 0u
        val lookup: List<Pair<UInt, T>> = map.map.entries.map {
            val value = values[it.value] ?: 0u
            threshold += value
            Pair(threshold, it.key)
        }.toList()

        val index = numberGenerator.getNumber() % threshold

        lookup.forEach {
            if (index < it.first) {
                return it.second
            }
        }

        return null
    }

}