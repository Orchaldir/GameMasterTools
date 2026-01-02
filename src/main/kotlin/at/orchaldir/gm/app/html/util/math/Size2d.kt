package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldSize(name: String, size: Size2d) {
    field(name, "${size.width} x ${size.height}")
}

// edit

fun HtmlBlockTag.selectSize(
    param: String,
    size: Size2d,
    minValue: Distance,
    maxVale: Distance,
    prefix: SiPrefix,
) = selectSize(
    param,
    size,
    minValue.convertToLong(prefix),
    maxVale.convertToLong(prefix),
    prefix,
)

fun HtmlBlockTag.selectSize(
    param: String,
    size: Size2d,
    minValue: Long,
    maxVale: Long,
    prefix: SiPrefix,
) {
    selectDistance("Width", combine(param, WIDTH), size.width, minValue, maxVale, prefix)
    selectDistance("Height", combine(param, HEIGHT), size.height, minValue, maxVale, prefix)
}

// parse

fun parseSize(
    parameters: Parameters,
    param: String,
    prefix: SiPrefix,
    default: Distance,
) = Size2d(
    parseDistance(parameters, combine(param, WIDTH), prefix, default),
    parseDistance(parameters, combine(param, HEIGHT), prefix, default),
)


