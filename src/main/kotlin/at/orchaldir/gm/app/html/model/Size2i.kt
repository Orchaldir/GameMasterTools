package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseInt

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2i
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldSize(name: String, size: Size2i) {
    field(name, "${size.width} mm x ${size.height} mm")
}

// edit

fun HtmlBlockTag.selectSize(
    param: String,
    size: Size2i,
    minValue: Distance,
    maxVale: Distance,
    stepValue: Distance = Distance(1),
    update: Boolean = false,
) {
    selectDistance("Width", combine(param, WIDTH), Distance(size.width), minValue, maxVale, stepValue, update)
    selectDistance("Height", combine(param, HEIGHT), Distance(size.height), minValue, maxVale, stepValue, update)
}

// parse

fun parseSize(
    parameters: Parameters,
    param: String,
) = Size2i(parseInt(parameters, combine(param, WIDTH), 1), parseInt(parameters, combine(param, HEIGHT), 1))


