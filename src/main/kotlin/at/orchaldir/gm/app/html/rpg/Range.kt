package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.MAX
import at.orchaldir.gm.app.MIN
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.utils.math.IntRange
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

private val DEFAULT_INT_RANGE = IntRange(-100, 100)

// show

fun HtmlBlockTag.fieldRange(
    label: String,
    range: IntRange,
) {
    field(label, "${range.min} to ${range.max}")
}

fun HtmlBlockTag.showRange(
    label: String,
    range: IntRange,
) {
    field("Min $label", range.min)
    field("Max $label", range.max)
}

// edit

fun HtmlBlockTag.editRange(
    label: String,
    value: IntRange,
    param: String,
    range: IntRange = DEFAULT_INT_RANGE,
) {
    selectInt(
        "Min $label",
        value.min,
        range.min,
        value.max - 1,
        1,
        combine(param, MIN),
    )
    selectInt(
        "Max $label",
        value.max,
        value.min + 1,
        range.max,
        1,
        combine(param, MAX),
    )
}

fun HtmlBlockTag.selectFromRange(
    label: String,
    range: IntRange,
    value: Int,
    param: String,
    stepNumber: Int = 1,
) {
    selectInt(
        label,
        value,
        range.min,
        range.max,
        stepNumber,
        param,
    )
}

// parse

fun parseRange(
    parameters: Parameters,
    param: String,
) = IntRange(
    parseInt(parameters, combine(param, MIN)),
    parseInt(parameters, combine(param, MAX)),
)
