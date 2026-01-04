package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.COST
import at.orchaldir.gm.app.html.util.math.fieldFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
import at.orchaldir.gm.core.model.rpg.combat.MAX_COST_FACTOR
import at.orchaldir.gm.core.model.rpg.combat.MIN_COST_FACTOR
import at.orchaldir.gm.utils.math.Factor
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldCostFactor(factor: Factor) =
    fieldFactor("Cost", factor)


// edit

fun HtmlBlockTag.selectCostFactor(factor: Factor) {
    selectFactor(
        "Cost",
        COST,
        factor,
        MIN_COST_FACTOR,
        MAX_COST_FACTOR,
    )
}

// parse

private fun parseCostFactor(
    parameters: Parameters,
    default: Factor,
) = parseFactor(parameters, COST, default)
