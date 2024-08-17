package at.orchaldir.gm.utils.math

import kotlin.math.absoluteValue
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