package at.orchaldir.gm.utils.math.unit

import at.orchaldir.gm.utils.math.Factor

const val SI_STEP = 10
const val SI_THREE_STEPS = 1000
const val SI_FOUR_STEPS = SI_THREE_STEPS * SI_STEP
const val SI_SIX_STEPS = SI_THREE_STEPS * SI_THREE_STEPS
const val SI_NINE_STEPS = SI_SIX_STEPS * SI_THREE_STEPS

interface SiUnit<T> {

    fun value(): Long
    fun convertToLong(prefix: SiPrefix): Long

    operator fun plus(other: T): T
    operator fun minus(other: T): T
    operator fun times(factor: Factor): T

}

fun down(value: Long) = value * SI_STEP
fun down(value: Float) = (value * SI_STEP).toLong()

fun downThreeSteps(value: Long) = value * SI_THREE_STEPS
fun downThreeSteps(value: Float) = (value * SI_THREE_STEPS).toLong()

fun downSixSteps(value: Long) = value * SI_SIX_STEPS
fun downSixSteps(value: Float) = (value * SI_SIX_STEPS).toLong()

fun downNineSteps(value: Long) = value * SI_NINE_STEPS

fun up(value: Int) = value / SI_STEP.toFloat()
fun up(value: Long) = value / SI_STEP.toFloat()
fun up(value: Float) = value / SI_STEP.toFloat()

fun upThreeSteps(value: Long) = value / SI_THREE_STEPS.toFloat()
fun upThreeSteps(value: Float) = value / SI_THREE_STEPS.toFloat()

fun upSixSteps(value: Long) = value / SI_SIX_STEPS.toFloat()
fun upSixSteps(value: Float) = value / SI_SIX_STEPS.toFloat()

fun upNineSteps(value: Long) = value / SI_NINE_STEPS.toFloat()