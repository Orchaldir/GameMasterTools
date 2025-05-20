package at.orchaldir.gm.app.html.model.util

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectLong
import at.orchaldir.gm.utils.math.unit.Orientation
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromMillidegrees
import at.orchaldir.gm.utils.math.unit.ZERO_ORIENTATION
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldOrientation(name: String, orientation: Orientation) {
    field(name, orientation.toString())
}

// edit

fun HtmlBlockTag.selectOrientation(
    label: String,
    param: String,
    current: Orientation,
    minValue: Orientation,
    maxValue: Orientation,
    step: Orientation = fromMillidegrees(1),
) {
    field(label) {
        selectOrientation(param, current, minValue, maxValue, step)
    }
}

fun HtmlBlockTag.selectOrientation(
    param: String,
    current: Orientation,
    minValue: Orientation,
    maxValue: Orientation,
    step: Orientation = fromMillidegrees(1),
) {
    selectLong(
        current.value(),
        minValue.value(),
        maxValue.value(),
        step.value(),
        param,
    )
    +current.toString()
}

// parse

fun parseOrientation(parameters: Parameters, param: String, default: Orientation = ZERO_ORIENTATION) =
    parameters[param]?.toLong()?.let { fromMillidegrees(it) } ?: default