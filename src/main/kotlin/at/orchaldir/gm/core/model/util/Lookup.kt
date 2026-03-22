package at.orchaldir.gm.core.model.util

import kotlinx.serialization.Serializable

@Serializable
data class LookupEntry<T>(
    val value: T,
    val until: Int,
)

@Serializable
data class Lookup<T>(
    val entries: List<LookupEntry<T>> = emptyList(),
) {
    constructor(value: T) : this(listOf(LookupEntry(value, 1)))

    constructor(current: T, previous: LookupEntry<T>) : this(listOf(previous, LookupEntry(current, previous.until + 1)))

    fun get(number: Int): T {
        entries.forEach { (value, until) ->
            if (number <= until) {
                return value
            }
        }

        return entries.last().value
    }

}
