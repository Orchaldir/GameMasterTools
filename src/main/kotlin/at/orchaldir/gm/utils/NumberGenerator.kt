package at.orchaldir.gm.utils

import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
sealed class NumberGenerator {
    abstract fun getInt(until: Int = Int.MAX_VALUE): Int

    fun getInt(from: Int, until: Int) = from + getInt(until - from)

    fun getFloat(from: Float, until: Float) = from + getInt().toFloat() * (until - from) / Int.MAX_VALUE

    fun isTriggered(probability: Factor) = getFloat(0.0f, 1.0f) < probability.toNumber()

    fun <T> select(list: List<T>) = list[getInt(list.size)]
}

data class RandomNumberGenerator(val random: Random) : NumberGenerator() {
    override fun getInt(until: Int) = random.nextInt(0, until)
}

data class FixedNumberGenerator(val numbers: List<Int>, var index: Int = 0) : NumberGenerator() {
    override fun getInt(until: Int) = numbers[index++ % numbers.size] % until
}

data class Counter(var index: Int = 0) : NumberGenerator() {
    override fun getInt(until: Int) = index++ % until
}