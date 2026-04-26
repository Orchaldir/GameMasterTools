package at.orchaldir.gm.utils.math

interface Value<T> {

    fun value(): Long

    operator fun plus(other: T): T
    operator fun minus(other: T): T

    operator fun compareTo(other: T): Int

}
