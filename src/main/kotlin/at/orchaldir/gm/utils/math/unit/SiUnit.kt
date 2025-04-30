package at.orchaldir.gm.utils.math.unit

const val SI_FACTOR = 1000
const val SI_SQUARED = SI_FACTOR * SI_FACTOR

interface SiUnit<T> {

    fun value(): Int

    operator fun plus(other: T): T
    operator fun minus(other: T): T

}

fun down(value: Int) = value * SI_FACTOR
fun down(value: Float) = (value * SI_FACTOR).toInt()

fun downTwice(value: Int) = value * SI_SQUARED
fun downTwice(value: Float) = (value * SI_SQUARED).toInt()

fun up(value: Int) = value / SI_FACTOR.toFloat()
fun up(value: Float) = value / SI_FACTOR.toFloat()

fun upTwice(value: Int) = value / SI_SQUARED.toFloat()
fun upTwice(value: Float) = value / SI_SQUARED.toFloat()