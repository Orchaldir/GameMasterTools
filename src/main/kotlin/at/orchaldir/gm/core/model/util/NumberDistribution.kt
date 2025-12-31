package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

@Serializable
data class NumberDistribution<T>(
    val map: Map<T, Int> = emptyMap(),
    val unknown: Int = 0,
) {
    fun calculateTotal() = map.values.sum() + unknown
    fun getPercentage(id: T, total: Int) = Factor.divideTwoInts(getNumber(id), total)
    fun getPercentage(id: T) = getPercentage(id, calculateTotal())
    fun getNumber(id: T) = map.getOrDefault(id, 0)

    fun getData(id: T) = map[id]?.let { number ->
        Pair(number, Factor.divideTwoInts(number, calculateTotal()))
    }
}