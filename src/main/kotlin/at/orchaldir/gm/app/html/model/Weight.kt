package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.utils.math.unit.Weight
import at.orchaldir.gm.utils.math.unit.formatWeight
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldWeight(name: String, weight: Weight) {
    field(name, weight.toString())
}

// edit

fun HtmlBlockTag.selectWeight(
    label: String,
    param: String,
    current: Weight,
    minValue: Weight,
    maxValue: Weight,
    step: Weight = Weight.fromGram(1),
    update: Boolean = false,
) {
    field(label) {
        selectWeight(param, current, minValue, maxValue, step, update)
    }
}

fun HtmlBlockTag.selectWeight(
    param: String,
    current: Weight,
    minValue: Weight,
    maxValue: Weight,
    step: Weight = Weight.fromGram(1),
    update: Boolean = false,
) {
    val values = (minValue.value()..maxValue.value() step step.value()).toList()
    selectValue(param, values, update) { v ->
        label = formatWeight(v)
        value = v.toString()
        selected = v == current.value()
    }
}

// parse

fun parseWeight(
    parameters: Parameters,
    param: String,
    default: Int = 0,
) = Weight.fromGram(parseInt(parameters, param, default))
