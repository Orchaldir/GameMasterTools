package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.util.ONE_OF_RARITIES
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.util.RarityMap
import at.orchaldir.gm.utils.NumberGenerator
import at.orchaldir.gm.utils.RepeatableNumberGenerator

data class RarityGenerator(val values: Map<Rarity, Int>) {

    companion object {

        fun empty(step: Int = 1): RarityGenerator {
            require(step > 0) { "The step needs to be greater than 0!" }
            var value = 0
            return RarityGenerator(ONE_OF_RARITIES.toList().reversed().associateWith {
                val current = value
                value += step
                current
            })
        }

    }

    init {
        ONE_OF_RARITIES.filter { it != Rarity.Unavailable }
            .forEach {
                val value = values[it]
                require(value != null && value > 0) { "Rarity $it has no valid value!" }
            }
    }

    fun <T> generate(map: RarityMap<T>, numberGenerator: NumberGenerator) =
        generate(map, numberGenerator.getInt())

    fun <T> generate(map: RarityMap<T>, numberGenerator: RepeatableNumberGenerator, input: Int) =
        generate(map, numberGenerator.getInt(input))

    private fun <T> generate(map: RarityMap<T>, randomNumber: Int): T {
        val pair = calculateLookupMap(map)
        val threshold = pair.first

        return select(threshold, pair.second, randomNumber)
    }

    private fun <T> select(
        threshold: Int,
        lookup: List<Pair<Int, T>>,
        randomNumber: Int,
    ): T {
        val index = randomNumber % threshold

        lookup.forEach {
            if (index < it.first) {
                return it.second
            }
        }

        error("There should always be a valid value!")
    }

    private fun <T> calculateLookupMap(map: RarityMap<T>): Pair<Int, List<Pair<Int, T>>> {
        var threshold = 0
        val lookup: List<Pair<Int, T>> = map.getRarityMap()
            .entries
            .map {
                val value = values[it.value] ?: 0
                threshold += value
                Pair(threshold, it.key)
            }.toList()

        return Pair(threshold, lookup)
    }

}