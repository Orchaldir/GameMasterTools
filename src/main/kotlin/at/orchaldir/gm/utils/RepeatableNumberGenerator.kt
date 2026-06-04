package at.orchaldir.gm.utils

import kotlinx.serialization.Serializable

@Serializable
sealed class RepeatableNumberGenerator {

    abstract fun getInt(input: Int): Int

    fun getInt(input: Int, until: Int) = getInt(input) % until

    fun getInt(input: Int, from: Int, until: Int) = from + getInt(input, until - from)
}

data class NumberLookup(val numbers: Map<Int, Int>, var default: Int = 0) : RepeatableNumberGenerator() {

    override fun getInt(input: Int) = numbers.getOrDefault(input, default)

}

data class HashNumberGenerator(val seed: List<Int>) : RepeatableNumberGenerator() {

    override fun getInt(input: Int) = (seed + input).hashCode()

}
