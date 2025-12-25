package at.orchaldir.gm.core.model.util.population

import at.orchaldir.gm.core.selector.util.PopulationEntry
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ONE
import at.orchaldir.gm.utils.math.ZERO
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class ElementDistribution<T>(
    val map: Map<T, Factor> = emptyMap(),
) {
    fun getPercentage(id: T) = map.getOrDefault(id, ZERO)
    fun getNumber(total: Int, id: T) = getPercentage(id).apply(total)

    fun getDefinedPercentages() = map.values
        .reduceOrNull { sum, percentage -> sum + percentage } ?: ZERO
    fun getUndefinedPercentages() = ONE - getDefinedPercentages()

    fun getData(id: T, total: Int) = map[id]?.let { percentage ->
        Pair(percentage.apply(total), percentage)
    }
}