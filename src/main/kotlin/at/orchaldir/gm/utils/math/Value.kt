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

fun <T> validate(
    value: Value<T>,
    label: String,
    min: T,
    max: T,
) {
    require(value >= min) { "The $label is too small!" }
    require(value <= max) { "The $label is too large!" }
}
