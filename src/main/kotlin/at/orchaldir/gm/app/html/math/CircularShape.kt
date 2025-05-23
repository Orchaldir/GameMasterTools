package at.orchaldir.gm.app.html.math

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.utils.math.shape.CircularShape
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCircularShape(
    shape: CircularShape,
    label: String = "Shape",
) {
    field(label, shape)
}

// edit

fun HtmlBlockTag.selectCircularShape(
    shape: CircularShape,
    param: String,
    label: String = "Shape",
) {
    selectValue(label, param, CircularShape.entries, shape)
}

// parse

fun parseCircularShape(
    parameters: Parameters,
    param: String,
    default: CircularShape = CircularShape.Circle,
) = parse(parameters, param, default)
