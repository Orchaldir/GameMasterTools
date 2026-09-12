package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.app.html.tdString
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import kotlinx.html.HtmlBlockTag
import kotlinx.html.br
import kotlinx.html.table
import kotlinx.html.th
import kotlinx.html.tr

// show

fun HtmlBlockTag.fieldFactor(label: String, factor: Factor) {
    field(label, factor.toString())
}

fun HtmlBlockTag.showFactorMap(
    call: ApplicationCall,
    state: State,
    factors: Map<Id<*>, Factor>,
    label: String,
) {
    var totalFactor = FULL

    if (factors.isEmpty()) {
        return
    }

    br { }
    table {
        tr {
            th { +label }
            th { +"Value" }
        }
        tr {
            tdString("Base")
            tdString(FULL.toString())
        }
        factors.entries
            .sortedByDescending { it.value.toPermyriad() }
            .forEach { (id, factor) ->

                tr {
                    tdLink(call, state, id)
                    tdString(factor.toString())
                }

                totalFactor += factor
            }

        tr {
            tdString("Total")
            tdString(totalFactor.toString())
        }
    }
}

// edit

fun HtmlBlockTag.selectPercentage(
    label: String,
    param: String,
    current: Factor,
    minValue: Int,
    maxValue: Int,
    step: Int = 1,
) = selectFactor(
    label,
    param,
    current,
    fromPercentage(minValue),
    fromPercentage(maxValue),
    fromPercentage(step),
)

fun HtmlBlockTag.selectFactor(
    label: String,
    param: String,
    current: Factor,
    minValue: Factor = ZERO,
    maxValue: Factor = ONE,
    step: Factor = ONE_PERCENT,
) {
    field(label) {
        selectFactor(param, current, minValue, maxValue, step)
    }
}

fun HtmlBlockTag.selectFactor(
    param: String,
    current: Factor,
    minValue: Factor,
    maxValue: Factor,
    stepValue: Factor = ONE_PERCENT,
) {
    val values = (minValue.toPermyriad()..maxValue.toPermyriad() step stepValue.toPermyriad()).toList()
    selectValue(param, values) { v ->
        label = formatAsFactor(v)
        value = v.toString()
        selected = v == current.toPermyriad()
    }
}

// parse

fun parseFactor(
    parameters: Parameters,
    param: String,
    default: Factor = FULL,
) = parameters[param]?.toInt()?.let { Factor.fromPermyriad(it) } ?: default
