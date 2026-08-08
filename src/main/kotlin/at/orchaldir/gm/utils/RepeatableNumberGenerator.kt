package at.orchaldir.gm.utils

import at.orchaldir.gm.utils.math.modulo
import kotlinx.serialization.Serializable
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.time.Instant

const val COLOR_INDEX = 0

private val MD = MessageDigest.getInstance("MD5")

@Serializable
sealed class RepeatableNumberGenerator {

    abstract fun getInt(index: Int): Int

    fun getInt(index: Int, until: Int) = getInt(index)
        .modulo(until)

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

    override fun getInt(index: Int): Int {
        val buffer = ByteBuffer.allocate(Int.SIZE_BYTES * (seeds.size + 1))

        seeds.forEach { buffer.putInt(it) }

        buffer.putInt(index)

        val digest = MD.digest(buffer.array())

        return ByteBuffer.wrap(digest).int
    }

    override fun addSeed(seed: Int) = copy(seeds = seeds + seed)

}
