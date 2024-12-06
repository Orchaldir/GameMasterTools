package at.orchaldir.gm.utils.math

import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.log10
import kotlin.math.sign

fun Int.ceilDiv(other: Int): Int {
    return this.floorDiv(other) + this.rem(other).sign.absoluteValue
}

fun Int.modulo(size: Int) = if (this >= 0) {
    this % size
} else {
    val modulo = (this + 1) % size
    size + modulo - 1
}

fun Int.length() = when (this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}