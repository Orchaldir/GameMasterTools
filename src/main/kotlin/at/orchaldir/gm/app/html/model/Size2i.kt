package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.utils.math.Size2i
import at.orchaldir.gm.utils.math.unit.SiPrefix
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
    minValue: Int,
    maxVale: Int,
    prefix: SiPrefix,
    update: Boolean = false,
) {
    selectDistance("Width", combine(param, WIDTH), size.width, minValue, maxVale, prefix, update)
    selectDistance("Height", combine(param, HEIGHT), size.height, minValue, maxVale, prefix, update)
}

// parse

fun parseSize(
    parameters: Parameters,
    param: String,
    prefix: SiPrefix,
) = Size2i(
    parseDistance(parameters, combine(param, WIDTH), prefix, 1),
    parseDistance(parameters, combine(param, HEIGHT), prefix, 1),
)


