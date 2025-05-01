package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseLong
import at.orchaldir.gm.app.html.selectLong
import at.orchaldir.gm.utils.math.unit.SiPrefix
import at.orchaldir.gm.utils.math.unit.Weight
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldWeight(name: String, weight: Weight) {
    field(name, weight.toString())
}

// edit

fun HtmlBlockTag.selectWeight(
    label: String,
    param: String,
    current: Weight,
    minValue: Long,
    maxValue: Long,
    prefix: SiPrefix,
    update: Boolean = false,
) {
    field(label) {
        selectWeight(param, current, minValue, maxValue, prefix, update)
    }
}

fun HtmlBlockTag.selectWeight(
    param: String,
    current: Weight,
    minValue: Long,
    maxValue: Long,
    prefix: SiPrefix,
    update: Boolean = false,
) {
    val unit = Weight.resolveUnit(prefix)
    val text = current.toString()
    val currentValue = current.convertToLong(prefix)
    selectLong(currentValue, minValue, maxValue, 1, param, update)
    +"$unit ($text)"
}

// parse

fun parseWeight(
    parameters: Parameters,
    param: String,
    prefix: SiPrefix,
    default: Long = 0,
) = Weight.from(prefix, parseLong(parameters, param, default))
