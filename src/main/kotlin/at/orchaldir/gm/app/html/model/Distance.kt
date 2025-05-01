package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldDistance(name: String, distance: Distance) {
    field(name, distance.toString())
}

// edit

fun HtmlBlockTag.selectDistance(
    label: String,
    param: String,
    distance: Distance,
    minValue: Distance,
    maxValue: Distance,
    prefix: SiPrefix,
    update: Boolean = false,
) {
    field(label) {
        selectDistance(param, distance, minValue, maxValue, prefix, update)
    }
}

fun HtmlBlockTag.selectDistance(
    param: String,
    current: Distance,
    minValue: Distance,
    maxValue: Distance,
    prefix: SiPrefix,
    update: Boolean = false,
) = selectDistance(
    param,
    current,
    minValue.convertTo(prefix).toInt(),
    maxValue.convertTo(prefix).toInt(),
    prefix,
    update,
)

fun HtmlBlockTag.selectDistance(
    label: String,
    param: String,
    distance: Distance,
    minValue: Int,
    maxValue: Int,
    prefix: SiPrefix,
    update: Boolean = false,
) {
    field(label) {
        selectDistance(param, distance, minValue, maxValue, prefix, update)
    }
}

fun HtmlBlockTag.selectDistance(
    param: String,
    current: Distance,
    minValue: Int,
    maxValue: Int,
    prefix: SiPrefix,
    update: Boolean = false,
) {
    val unit = Distance.resolveUnit(prefix)
    val text = current.toString()
    val currentValue = current.convertTo(prefix).toInt()
    selectInt(currentValue, minValue, maxValue, 1, param, update)
    +"$unit ($text)"
}

// parse

fun parseDistance(
    parameters: Parameters,
    param: String,
    prefix: SiPrefix,
    default: Int = 0,
) = Distance.from(prefix, parseInt(parameters, param, default))

fun parseDistance(
    parameters: Parameters,
    param: String,
    prefix: SiPrefix,
    default: Distance,
) = parseDistance(parameters, param, prefix, default.convertTo(prefix).toInt())
