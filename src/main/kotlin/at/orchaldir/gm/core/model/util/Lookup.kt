package at.orchaldir.gm.core.model.util

import kotlinx.serialization.Serializable

@Serializable
data class LookupEntry<T>(
    val value: T,
    val until: Int,
)

@Serializable
data class Lookup<T>(
    val current: T,
    val previousEntries: List<LookupEntry<T>> = emptyList(),
) {
    constructor(current: T) : this(current, emptyList())

    constructor(current: T, previousEntry: LookupEntry<T>) : this(current, listOf(previousEntry))

    fun get(number: Int): T {
        previousEntries.forEach { (value, until) ->
            if (number <= until) {
                return value
            }
        }

        return current
    }

}
