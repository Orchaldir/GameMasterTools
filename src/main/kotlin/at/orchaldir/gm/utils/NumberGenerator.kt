package at.orchaldir.gm.utils

import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.random.nextUInt

@Serializable
sealed class NumberGenerator {
    abstract fun getNumber(): UInt;
}

data class RandomNumberGenerator(val random: Random) : NumberGenerator() {
    override fun getNumber() = random.nextUInt()
}

data class FixedNumberGenerator(val numbers: List<UInt>, var index: Int = 0) : NumberGenerator() {
    override fun getNumber() = numbers[index++ % numbers.size]
}