package at.orchaldir.gm.app.html.math

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.shape.RectangularShape
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showCircularShape(
    shape: CircularShape,
    label: String = "Shape",
) {
    field(label, shape)
}

fun HtmlBlockTag.showRectangularShape(
    shape: RectangularShape,
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

fun HtmlBlockTag.selectRectangularShape(
    shape: RectangularShape,
    param: String,
    label: String = "Shape",
) {
    selectValue(label, param, RectangularShape.entries, shape)
}

// parse

fun parseCircularShape(
    parameters: Parameters,
    param: String,
    default: CircularShape = CircularShape.Circle,
) = parse(parameters, param, default)

fun parseRectangularShape(
    parameters: Parameters,
    param: String,
    default: RectangularShape = RectangularShape.Rectangle,
) = parse(parameters, param, default)
