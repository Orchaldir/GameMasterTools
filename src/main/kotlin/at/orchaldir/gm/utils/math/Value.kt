package at.orchaldir.gm.utils.math

import kotlin.text.compareTo

interface Value<T> {

    fun zero(): T

    fun value(): Long

    operator fun plus(other: T): T
    operator fun minus(other: T): T

    operator fun times(factor: Factor): T
    operator fun times(factor: Float): T

    operator fun compareTo(other: T): Int

    fun validate(
        label: String,
        min: T,
        max: T,
    ) {
        require(this >= min) { "The $label is too small!" }
        require(this <= max) { "The $label is too large!" }
    }

}
