package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.ZERO

val MIN_COST_FACTOR = Factor.fromPercentage(-100)
val DEFAULT_TYPE_COST_FACTOR = FULL
val DEFAULT_MODIFIER_COST_FACTOR = ZERO
val MAX_COST_FACTOR = Factor.fromNumber(100)

fun validate(cost: Factor) {
    require(cost >= MIN_COST_FACTOR) { "Cost Factor $cost is below the minimum!" }
    require(cost <= MAX_COST_FACTOR) { "Cost Factor $cost is above the maximum!" }
}