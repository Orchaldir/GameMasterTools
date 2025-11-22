package at.orchaldir.gm.core.reducer.rpg

import at.orchaldir.gm.core.model.rpg.Range

fun validateIsInside(value: Int, text: String, range: Range) =
    validateIsInside(value, text, range.min, range.max)

fun validateIsInside(value: Int, text: String, min: Int, max: Int) {
    require(value >= min) { "$text needs to be >= $min!" }
    require(value <= max) { "$text needs to be <= $max!" }
}