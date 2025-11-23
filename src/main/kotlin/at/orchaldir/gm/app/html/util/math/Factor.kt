package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.math.ONE_PERCENT
import at.orchaldir.gm.utils.math.formatAsFactor
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldFactor(label: String, factor: Factor) {
    field(label, factor.toString())
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
    minValue: Factor,
    maxValue: Factor,
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
