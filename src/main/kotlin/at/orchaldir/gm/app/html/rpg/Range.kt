package at.orchaldir.gm.app.html.rpg

import at.orchaldir.gm.app.MAX
import at.orchaldir.gm.app.MIN
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.rpg.Range
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldRange(
    label: String,
    range: Range,
) {
    field(label, "${range.min} to ${range.max}")
}

fun HtmlBlockTag.showRange(
    label: String,
    range: Range,
) {
    field("Min $label", range.min)
    field("Max $label", range.max)
}

// edit

fun HtmlBlockTag.editRange(
    label: String,
    range: Range,
    param: String,
) {
    selectInt(
        "Min $label",
        range.min,
        -100,
        range.max - 1,
        1,
        combine(param, MIN),
    )
    selectInt(
        "Max $label",
        range.max,
        range.min + 1,
        100,
        1,
        combine(param, MAX),
    )
}

fun HtmlBlockTag.selectFromRange(
    label: String,
    range: Range,
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
) = Range(
    parseInt(parameters, combine(param, MIN)),
    parseInt(parameters, combine(param, MAX)),
)
