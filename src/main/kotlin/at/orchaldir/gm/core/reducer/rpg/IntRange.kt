package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.utils.math.RangeInt

fun validateIsInside(value: Int, text: String, range: RangeInt) =
    validateIsInside(value, text, range.min, range.max)

fun validateIsInside(value: Int, text: String, min: Int, max: Int) {
    require(value >= min) { "$text needs to be >= $min!" }
    require(value <= max) { "$text needs to be <= $max!" }
}