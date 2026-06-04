package at.orchaldir.gm.utils

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
sealed class RepeatableNumberGenerator {

    abstract fun getInt(input: Int): Int

    fun getInt(input: Int, until: Int) = getInt(input) % until

    fun getInt(input: Int, from: Int, until: Int) = from + getInt(input, until - from)

    abstract fun addSeed(seed: Int): RepeatableNumberGenerator
}

data class NumberLookup(
    val unusedSeeds: List<Int>,
    val numbers: Map<Int, Int>,
    var default: Int = 0,
) : RepeatableNumberGenerator() {

    override fun getInt(input: Int) = numbers.getOrDefault(input, default)

    override fun addSeed(seed: Int) = copy(unusedSeeds = unusedSeeds + seed)

}

data class HashNumberGenerator(val seeds: List<Int>) : RepeatableNumberGenerator() {

    constructor(seed: Int) : this(listOf(seed))

    companion object {

        fun fromTime() = HashNumberGenerator(Instant.now().epochSecond.toInt())

    }

    override fun getInt(input: Int) = (seeds + input).hashCode()

    override fun addSeed(seed: Int) = copy(seeds = seeds + seed)

}
