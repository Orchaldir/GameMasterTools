package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.utils.map.MapSize2d
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldMapSize(name: String, size: MapSize2d) {
    field(name, size.format())
}

// edit

fun HtmlBlockTag.selectMapSize(
    param: String,
    size: MapSize2d,
    minValue: Int,
    maxVale: Int,
) {
    selectInt(
        "Width",
        size.width,
        minValue,
        maxVale,
        1,
        combine(param, WIDTH),
    )
    selectInt(
        "Height",
        size.width,
        minValue,
        maxVale,
        1,
        combine(param, HEIGHT),
    )
}

// parse

fun parseMapSize(
    parameters: Parameters,
    param: String,
    default: Int,
) = MapSize2d(
    parseInt(parameters, combine(param, WIDTH), default),
    parseInt(parameters, combine(param, HEIGHT), default),
)


