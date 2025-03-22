package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Distance.Companion.fromMillimeters
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldSize(name: String, size: Size2i) {
    field(name, "${size.width} x ${size.height}")
}

// edit

fun HtmlBlockTag.selectSize(
    param: String,
    size: Size2i,
    minValue: Distance,
    maxVale: Distance,
    stepValue: Distance = fromMillimeters(1),
    update: Boolean = false,
) {
    selectDistance("Width", combine(param, WIDTH), size.width, minValue, maxVale, stepValue, update)
    selectDistance("Height", combine(param, HEIGHT), size.height, minValue, maxVale, stepValue, update)
}

// parse

fun parseSize(
    parameters: Parameters,
    param: String,
) = Size2i(parseDistance(parameters, combine(param, WIDTH), 1), parseDistance(parameters, combine(param, HEIGHT), 1))


