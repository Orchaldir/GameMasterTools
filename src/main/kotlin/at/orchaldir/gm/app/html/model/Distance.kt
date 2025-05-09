package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseLong
import at.orchaldir.gm.app.html.selectLong
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
    minValue.convertToLong(prefix),
    maxValue.convertToLong(prefix),
    prefix,
    update,
)

fun HtmlBlockTag.selectDistance(
    label: String,
    param: String,
    distance: Distance,
    minValue: Long,
    maxValue: Long,
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
    minValue: Long,
    maxValue: Long,
    prefix: SiPrefix,
    update: Boolean = false,
) {
    val unit = Distance.resolveUnit(prefix)
    val text = current.toString()
    val currentValue = current.convertToLong(prefix)
    selectLong(currentValue, minValue, maxValue, 1, param, update)
    +"$unit ($text)"
}

// parse

fun parseDistance(
    parameters: Parameters,
    param: String,
    prefix: SiPrefix,
    default: Long = 0,
) = Distance.from(prefix, parseLong(parameters, param, default))

fun parseDistance(
    parameters: Parameters,
    param: String,
    prefix: SiPrefix,
    default: Distance,
) = parseDistance(parameters, param, prefix, default.convertToLong(prefix))
