package at.orchaldir.gm.app.html.util.math

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseLong
import at.orchaldir.gm.app.html.selectLong
import at.orchaldir.gm.utils.math.unit.SiPrefix
import at.orchaldir.gm.utils.math.unit.Weight
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldWeight(name: String, weight: Weight?) {
    if (weight != null) {
        field(name, weight.toString())
    }
}

// edit

fun HtmlBlockTag.selectWeight(
    label: String,
    param: String,
    current: Weight,
    minValue: Long,
    maxValue: Long,
    prefix: SiPrefix,
) {
    field(label) {
        selectWeight(param, current, minValue, maxValue, prefix)
    }
}

fun HtmlBlockTag.selectWeight(
    param: String,
    current: Weight,
    minValue: Long,
    maxValue: Long,
    prefix: SiPrefix,
) {
    val unit = Weight.resolveUnit(prefix)
    val text = current.toString()
    val currentValue = current.convertToLong(prefix)
    selectLong(currentValue, minValue, maxValue, 1, param)
    +"$unit ($text)"
}

// parse

fun parseWeight(
    parameters: Parameters,
    param: String,
    prefix: SiPrefix,
    default: Long = 0,
) = Weight.from(prefix, parseLong(parameters, param, default))
