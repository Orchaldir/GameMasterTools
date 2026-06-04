package at.orchaldir.gm.utils

import kotlinx.serialization.Serializable
import java.time.Clock
import java.time.Instant

@Serializable
sealed class RepeatableNumberGenerator {

    abstract fun getInt(input: Int): Int

    fun getInt(input: Int, until: Int) = getInt(input) % until

    fun getInt(input: Int, from: Int, until: Int) = from + getInt(input, until - from)
}

data class NumberLookup(
    val unUsedSeed: List<Int>,
    val numbers: Map<Int, Int>,
    var default: Int = 0,
) : RepeatableNumberGenerator() {

    override fun getInt(input: Int) = numbers.getOrDefault(input, default)

}

data class HashNumberGenerator(val seed: List<Int>) : RepeatableNumberGenerator() {

    constructor(seed: Int) : this(listOf(seed))

    companion object {

        fun fromTime() = HashNumberGenerator(Instant.now().epochSecond.toInt())

    }

    override fun getInt(input: Int) = (seed + input).hashCode()

}
