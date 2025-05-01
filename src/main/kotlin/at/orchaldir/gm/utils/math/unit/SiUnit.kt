package at.orchaldir.gm.utils.math.unit

const val SI_STEP = 10
const val SI_FACTOR = 1000
const val SI_SQUARED = SI_FACTOR * SI_FACTOR

interface SiUnit<T> {

    fun value(): Long
    fun convertTo(prefix: SiPrefix): Long

    operator fun plus(other: T): T
    operator fun minus(other: T): T

}

fun down(value: Int) = value.toLong() * SI_STEP
fun down(value: Long) = value * SI_STEP
fun down(value: Float) = (value * SI_STEP).toLong()

fun downThreeSteps(value: Int) = value.toLong() * SI_FACTOR
fun downThreeSteps(value: Long) = value * SI_FACTOR
fun downThreeSteps(value: Float) = (value * SI_FACTOR).toLong()

fun downSixSteps(value: Int) = value.toLong() * SI_SQUARED
fun downSixSteps(value: Long) = value * SI_SQUARED
fun downSixSteps(value: Float) = (value * SI_SQUARED).toLong()

fun up(value: Int) = value / SI_STEP.toFloat()
fun up(value: Long) = value / SI_STEP.toFloat()
fun up(value: Float) = value / SI_STEP.toFloat()

fun upThreeSteps(value: Int) = value / SI_FACTOR.toFloat()
fun upThreeSteps(value: Long) = value / SI_FACTOR.toFloat()
fun upThreeSteps(value: Float) = value / SI_FACTOR.toFloat()

fun upSixSteps(value: Int) = value / SI_SQUARED.toFloat()
fun upSixSteps(value: Long) = value / SI_SQUARED.toFloat()
fun upSixSteps(value: Float) = value / SI_SQUARED.toFloat()

fun upNineSteps(value: Long) = value / (SI_SQUARED * SI_FACTOR).toFloat()