package at.orchaldir.gm.utils

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
sealed class NumberGenerator {
    abstract fun getNumber(until: Int = Int.MAX_VALUE): Int

    fun <T> select(list: List<T>) = list[getNumber(list.size)]
}

data class RandomNumberGenerator(val random: Random) : NumberGenerator() {
    override fun getNumber(until: Int) = random.nextInt(0, until)
}

data class FixedNumberGenerator(val numbers: List<Int>, var index: Int = 0) : NumberGenerator() {
    override fun getNumber(until: Int) = numbers[index++ % numbers.size] % until
}

data class Counter(var index: Int = 0) : NumberGenerator() {
    override fun getNumber(until: Int) = index++ % until
}