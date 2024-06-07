package at.orchaldir.gm.utils

import kotlinx.serialization.Serializable
import mu.KotlinLogging
import kotlin.random.Random
import kotlin.random.nextUInt

private val logger = KotlinLogging.logger {}

@Serializable
sealed class NumberGenerator {
    abstract fun getNumber(): UInt

    fun <T> select(list: List<T>) = list[(getNumber() % list.size.toUInt()).toInt()]
}

data class RandomNumberGenerator(val random: Random) : NumberGenerator() {
    override fun getNumber() = random.nextUInt()
}

data class FixedNumberGenerator(val numbers: List<UInt>, var index: Int = 0) : NumberGenerator() {
    override fun getNumber() = numbers[index++ % numbers.size]
}

data class Counter(var index: UInt = 0u) : NumberGenerator() {
    override fun getNumber() = index++
}