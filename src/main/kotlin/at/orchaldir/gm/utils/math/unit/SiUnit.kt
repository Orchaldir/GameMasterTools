package at.orchaldir.gm.utils.math.unit

interface SiUnit<T> {

    fun value(): Int

    operator fun plus(other: T): T
    operator fun minus(other: T): T

}