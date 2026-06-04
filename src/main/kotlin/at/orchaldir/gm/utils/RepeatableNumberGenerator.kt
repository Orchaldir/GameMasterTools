package at.orchaldir.gm.utils

import kotlinx.serialization.Serializable
import java.time.Instant

const val COLOR_INDEX = 0

@Serializable
sealed class RepeatableNumberGenerator {

    abstract fun getInt(index: Int): Int

    fun getInt(index: Int, until: Int) = getInt(index) % until

    fun getInt(index: Int, from: Int, until: Int) = from + getInt(index, until - from)

    abstract fun addSeed(seed: Int): RepeatableNumberGenerator
}

data class NumberLookup(
    val unusedSeeds: List<Int>,
    val numbers: Map<Int, Int>,
    var default: Int = 0,
) : RepeatableNumberGenerator() {

    override fun getInt(index: Int) = numbers.getOrDefault(index, default)

    override fun addSeed(seed: Int) = copy(unusedSeeds = unusedSeeds + seed)

}

data class HashNumberGenerator(val seeds: List<Int>) : RepeatableNumberGenerator() {

    constructor(seed: Int) : this(listOf(seed))

    companion object {

        fun fromTime() = HashNumberGenerator(Instant.now().epochSecond.toInt())

    }

    override fun getInt(index: Int) = (seeds + index).hashCode()

    override fun addSeed(seed: Int) = copy(seeds = seeds + seed)

}
