package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.formatMillimetersAsMeters
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldDistance(name: String, distance: Distance) {
    field(name, distance.toString())
}

// edit

fun HtmlBlockTag.selectDistance(
    label: String,
    param: String,
    distance: Distance,
    minValue: Distance,
    maxValue: Distance,
    step: Distance = Distance(1),
    update: Boolean = false,
) {
    field(label) {
        selectDistance(param, distance, minValue, maxValue, step, update)
    }
}

fun HtmlBlockTag.selectDistance(
    param: String,
    current: Distance,
    minValue: Distance,
    maxValue: Distance,
    step: Distance = Distance(1),
    update: Boolean = false,
) {
    val values = (minValue.value()..maxValue.value() step step.value()).toList()
    selectValue(param, values, update) { v ->
        label = formatMillimetersAsMeters(v)
        value = v.toString()
        selected = v == current.value()
    }
}

// parse

fun parseDistance(
    parameters: Parameters,
    param: String,
    default: Int = 0,
) = Distance(parseInt(parameters, param, default))
