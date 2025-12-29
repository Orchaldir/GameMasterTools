package at.orchaldir.gm.core.model.util.population

import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class NumberDistribution<T>(
    val map: Map<T, Int> = emptyMap(),
) {
    fun getTotal() = map.values.sum()
    fun getPercentage(id: T, total: Int) = Factor.divideTwoInts(getNumber(id), total)
    fun getNumber(id: T) = map.getOrDefault(id, 0)
}