package at.orchaldir.gm.utils.math

interface Value<T> {

    fun zero(): T

    fun value(): Long

    operator fun plus(other: T): T
    operator fun minus(other: T): T

    operator fun times(factor: Factor): T
    operator fun times(factor: Float): T

    operator fun compareTo(other: T): Int

}
